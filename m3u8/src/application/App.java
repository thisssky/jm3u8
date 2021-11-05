package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;

import application.component.ContextMenuTableRow;
import application.component.DirTableCell;
import application.component.DownloadColumn;
import application.component.MergeColumn;
import application.component.Toast;
import application.dto.EXTINF;
import application.dto.Message;
import application.dto.TableItem;
import application.dto.XMLRoot;
import application.utils.CommonUtility;
import application.utils.JAXBUtils;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class App extends Application {
	private Stage primaryStage;
	private TextField urlTextField;
	private TextField dirTextField;
	private Button downloadButton;
	private BorderPane root;
	private ListView<String> leftBox;
	private BorderPane rightBox;
	private TableView<TableItem> tableView;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				ObservableList<TableItem> items = tableView.getItems();
				for (TableItem item : items) {
					item.getDownloadColumn().suspend();
				}
				// 通知firefox插件关闭了
				try {
					sendMessage("{\"type\":\"app\"}");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		this.primaryStage = primaryStage;
		primaryStage.setMinWidth(650);
		primaryStage.setTitle("下载m3u8视频");
		ObservableList<javafx.scene.image.Image> icons = primaryStage.getIcons();
		icons.add(CommonUtility.getImage("title.png"));

		// 创建MenuBar
		MenuBar menuBar = new MenuBar();
		menuBar.setPadding(new Insets(0));
//		menuBar.setStyle("-fx-background-color:red");

		// 创建Menu
		Menu fileMenu = new Menu("文件");
		MenuItem open = new MenuItem("打开");
		fileMenu.getItems().add(open);
		Menu menu2 = new Menu("视图");
		Menu menu3 = new Menu("o");

		// Menu键入到MenuBar
//		menuBar.getMenus().addAll(fileMenu, menu2, menu3);

		root = new BorderPane();
		rightBox = new BorderPane();
		root.setCenter(rightBox);
		root.setTop(menuBar);

//		initLeft();
		initTop();
		initTableView();
		setOnAction();
		// 接收firefox插件的消息
		receive();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private static int getInt(byte[] bytes) {
		return (bytes[3] << 24) & 0xff000000 | (bytes[2] << 16) & 0x00ff0000 | (bytes[1] << 8) & 0x0000ff00
				| (bytes[0]) & 0x000000ff;
	}

	private void receive() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					while (true) {
						byte[] messageLength = new byte[4];
						System.in.read(messageLength);
						// 读取消息大小
						int size = getInt(messageLength);
						if (size == 0) {
							throw new InterruptedIOException("Blocked communication");
						}

						byte[] messageContent = new byte[size];
						System.in.read(messageContent);

						String string = new String(messageContent, StandardCharsets.UTF_8);
						ObjectMapper mapper = new ObjectMapper();
						Message message = mapper.readValue(string, Message.class);

						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
						String ymdhms = formatter.format(LocalDateTime.now());
						if (dirTextField.getText().trim().equals("")) {
							Toast toast = new Toast("请填写下载路径!", 3000);
							Platform.runLater(new Runnable() {

								@Override
								public void run() {
									toast.showBottom(primaryStage);
								}
							});
						} else {
							download(message.getUrl(), dirTextField.getText() + File.separator + ymdhms);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}).start();
	}

	private void sendMessage(String message) throws IOException {
		System.out.write(getBytes(message.length()));
		System.out.write(message.getBytes(StandardCharsets.UTF_8));
		System.out.flush();
	}

	private byte[] getBytes(int length) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (length & 0xFF);
		bytes[1] = (byte) ((length >> 8) & 0xFF);
		bytes[2] = (byte) ((length >> 16) & 0xFF);
		bytes[3] = (byte) ((length >> 24) & 0xFF);
		return bytes;
	}

	private void initLeft() {
		root.setOnMouseMoved(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				double x = event.getX();
				if (x < 50) {
					leftBox.setVisible(true);
					leftBox.setPrefWidth(200);
				}

			}
		});

		ObservableList<String> strList = FXCollections.observableArrayList();

		for (int i = 0; i < 10; i++) {
			EXTINF extinf = new EXTINF();
			extinf.setIndex(i);
			strList.add("/xxx/xxx/asdfasdfasdfasdfasdfasdfs" + i);
		}
		leftBox = new ListView<String>(strList);
		leftBox.setPrefWidth(200);
		leftBox.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

		root.setLeft(leftBox);

		leftBox.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

//		leftBox.getSelectionModel().selectIndices(1, 2);
//		leftBox.getFocusModel().focus(0);
		leftBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				Toast toast = new Toast(newValue);
				toast.showBottom(primaryStage);
			}
		});
		leftBox.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				leftBox.setPrefWidth(0);
				leftBox.setVisible(false);
			}
		});
	}

	private void initTop() {
		GridPane topGridPane = new GridPane();
		topGridPane.setVgap(5);
		rightBox.setTop(topGridPane);

		Label urlLabel = new Label("下载链接");
		urlLabel.setPrefWidth(60);
		urlLabel.setMinWidth(60);
		GridPane.setHalignment(urlLabel, HPos.RIGHT);
		GridPane.setMargin(urlLabel, new Insets(5, 0, 0, 5));
		topGridPane.add(urlLabel, 0, 0);

		urlTextField = new TextField();
		urlTextField.setPrefWidth(350);
		urlTextField.setPrefHeight(25);
		GridPane.setMargin(urlTextField, new Insets(5, 5, 0, 5));
		GridPane.setConstraints(urlTextField, 1, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		topGridPane.add(urlTextField, 1, 0);

		Label dirLabel = new Label("保存目录");
		dirLabel.setPrefWidth(60);
		dirLabel.setMinWidth(60);
		GridPane.setMargin(dirLabel, new Insets(5, 0, 5, 5));
		GridPane.setHalignment(dirLabel, HPos.RIGHT);
		topGridPane.add(dirLabel, 0, 1);

		dirTextField = new TextField();
		dirTextField.setPrefWidth(310);
		dirTextField.setPrefHeight(25);
		GridPane.setMargin(dirTextField, new Insets(5, 5, 5, 5));
		Tooltip tooltip = new Tooltip("双击、或按Enter键选择目录");
		tooltip.setFont(Font.font(16));
		dirTextField.setTooltip(tooltip);
		topGridPane.add(dirTextField, 1, 1);

		downloadButton = new Button("下载");
		downloadButton.setPrefHeight(60);
		downloadButton.setPrefWidth(60);
		downloadButton.setMinWidth(60);

		GridPane.setMargin(downloadButton, new Insets(0, 5, 0, 0));
		topGridPane.add(downloadButton, 2, 0, 1, 2);

		topGridPane.setOnDragEntered(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				List<File> files = event.getDragboard().getFiles();
				if (null == dirTextField.getText() || "".equals(dirTextField.getText())) {
					Toast toast = new Toast("请选择保存路径!", 3000);
					toast.showBottom(primaryStage);
				} else {
					BufferedReader bufferedReader = null;
					try {
						for (int i = 0; i < files.size(); i++) {
							File file = files.get(i);
							if (file.getName().equals("m3u8.txt")) {
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
								FileReader fileReader = new FileReader(file);
								bufferedReader = new BufferedReader(fileReader);
								Stream<String> lines = bufferedReader.lines();
								lines.forEach(item -> {
									String format = formatter.format(LocalDateTime.now());
									String dir = dirTextField.getText() + File.separator + format + "-"
											+ Math.abs(item.hashCode());
									File file2 = new File(dir);
									if (file2.exists()) {
										dir += "-re";
									}
									// 启动下载
									TableItem tableItem = new TableItem(item, dir);
									tableView.getItems().add(tableItem);
									tableItem.getDownloadColumn().download();
								});
							} else {
								Toast toast = new Toast("请使用m3u8.txt资源文件!", 3000);
								toast.showBottom(primaryStage);
							}
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} finally {
						try {
							bufferedReader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}

			}
		});
	}

	private void initTableView() {
		// https://blog.csdn.net/servermanage/article/details/102317726
		// https://blog.csdn.net/MrChung2016/article/details/71774496
		// https://docs.oracle.com/javafx/2/ui_controls/table-view.htm#CJABIEED
		// http://www.javafxchina.net/blog/2015/04/doc03_tableview/

		tableView = new TableView<TableItem>();
		rightBox.setCenter(tableView);
		// 可自由设置显示列
//		tableView.setTableMenuButtonVisible(true);
		tableView.setEditable(true);
		// 自动拉伸列，是所有的列占满整个表格
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		Label label = new Label("快来下载吧!!!");
		label.setFont(new Font(30));
		tableView.setPlaceholder(label);

//		TableViewSelectionModel<TableItem> selectionModel = tableView.getSelectionModel();
		// set selection mode to only 1 row
//		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		// set selection mode to multiple rows
//		selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
//		ObservableList<EXTINF_TYPE> selectedItems = selectionModel.getSelectedItems();
//		selectionModel.clearSelection();

		TableColumn<TableItem, String> indexColumn = new TableColumn<TableItem, String>("#");
		indexColumn.setSortNode(null);
		indexColumn.setPrefWidth(30);
		indexColumn.setMinWidth(30);
		indexColumn.setMaxWidth(30);
		indexColumn.setResizable(false);
		indexColumn.setSortable(false);
		indexColumn.setCellFactory(new Callback<TableColumn<TableItem, String>, TableCell<TableItem, String>>() {

			@Override
			public TableCell<TableItem, String> call(TableColumn<TableItem, String> param) {
				TableCell<TableItem, String> tableCell = new TableCell<TableItem, String>() {
					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						this.setText(null);
						this.setGraphic(null);
						this.setAlignment(Pos.CENTER_LEFT);
						if (!empty) {
							int rowIndex = this.getIndex() + 1;
							this.setText(String.valueOf(rowIndex));
						}
					}
				};
				return tableCell;
			}
		});

		TableColumn<TableItem, String> dirColumn = new TableColumn<TableItem, String>("保存路径");
		dirColumn.setMinWidth(200);
		dirColumn.setCellFactory(new Callback<TableColumn<TableItem, String>, TableCell<TableItem, String>>() {

			@Override
			public TableCell<TableItem, String> call(TableColumn<TableItem, String> param) {
				DirTableCell tableCell = new DirTableCell();
				return tableCell;
			}
		});
		dirColumn.setCellValueFactory(new PropertyValueFactory<TableItem, String>("dir"));

		TableColumn<TableItem, DownloadColumn> downloadColumn = new TableColumn<TableItem, DownloadColumn>("已下载");
		downloadColumn.setPrefWidth(208);
		downloadColumn.setMinWidth(208);
		downloadColumn.setMaxWidth(208);
		downloadColumn.setCellValueFactory(new PropertyValueFactory<TableItem, DownloadColumn>("downloadColumn"));

		TableColumn<TableItem, MergeColumn> fileSizeColumn = new TableColumn<TableItem, MergeColumn>("文件大小");
		fileSizeColumn.setPrefWidth(78);
		fileSizeColumn.setMinWidth(78);
		fileSizeColumn.setMaxWidth(78);
		fileSizeColumn.setCellValueFactory(new PropertyValueFactory<TableItem, MergeColumn>("mergeColumn"));

		TableColumn<TableItem, CheckBox> mergeColumn = new TableColumn<TableItem, CheckBox>("合并");
		mergeColumn.setSortable(false);
		mergeColumn.setPrefWidth(36);
		mergeColumn.setMinWidth(36);
		mergeColumn.setMaxWidth(36);
		mergeColumn.setStyle("-fx-alignment:center;");
		mergeColumn.setCellValueFactory(new PropertyValueFactory<TableItem, CheckBox>("mergeCheckBox"));

		TableColumn<TableItem, Button> mergeOptColumn = new TableColumn<TableItem, Button>("操作");
		mergeOptColumn.setSortable(false);
		mergeOptColumn.setPrefWidth(58);
		mergeOptColumn.setMinWidth(58);
		mergeOptColumn.setMaxWidth(58);
		mergeOptColumn.setCellValueFactory(new PropertyValueFactory<TableItem, Button>("mergeButton"));

		tableView.setRowFactory(new Callback<TableView<TableItem>, TableRow<TableItem>>() {

			@Override
			public TableRow<TableItem> call(TableView<TableItem> param) {
				ContextMenuTableRow extTableRow = new ContextMenuTableRow();
				return extTableRow;
			}
		});
		tableView.setOnDragEntered(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				List<File> files = event.getDragboard().getFiles();
				boolean flag = false;
				for (int i = 0; i < files.size(); i++) {
					File file = files.get(i);
					if (file.getName().equals(JAXBUtils.EXTINF_TYPE)) {
						ObservableList<TableItem> items = tableView.getItems();
						for (TableItem tableItem : items) {
							if (tableItem.getDir().equals(file.getParent())) {
								flag = true;
								Toast toast = new Toast("已存在!", 3000);
								toast.showBottom(primaryStage);
								break;
							}
						}
						if (flag) {
							break;
						} else {

							XMLRoot xmlRoot = JAXBUtils.read(file);
							TableItem tableItem = new TableItem(xmlRoot.getM3u8(), xmlRoot.getDir());
							tableView.getItems().add(tableItem);
							// 下载
							tableItem.getDownloadColumn().localDownload(xmlRoot);
						}
					} else {
						Toast toast = new Toast("文件格式不对!", 3000);
						toast.showBottom(primaryStage);
					}
				}

			}
		});
		tableView.getColumns().addAll(indexColumn, dirColumn, downloadColumn, fileSizeColumn, mergeColumn,
				mergeOptColumn);

//		ArrayList<TableItem> arrayList = new ArrayList<TableItem>();
//		for (int i = 0; i < 10; i++) {
//			TableItem progressContainer = new TableItem("m3u8" + i, "dir" + i);
//			arrayList.add(progressContainer);
//		}
//
//		TableItem progressContainer = new TableItem("m3u8", "C:\\Users\\kyh\\Desktop\\m3u8\\aj\\test");
//		arrayList.add(progressContainer);
//		ObservableList<TableItem> observableArrayList = FXCollections.observableArrayList(arrayList);
//		tableView.setItems(observableArrayList);
	}

	private void setOnAction() {
		dirTextField.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				KeyCode keyCode = event.getCode();
				if (KeyCode.ENTER == keyCode) {
					DirectoryChooser directoryChooser = new DirectoryChooser();
					directoryChooser.setTitle("选择文件夹");
					File dir = directoryChooser.showDialog(primaryStage);
					if (null != dir) {
						dirTextField.setText(dir.getAbsolutePath());
					}

				}

			}
		});
		dirTextField.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (2 == event.getClickCount()) {
					DirectoryChooser directoryChooser = new DirectoryChooser();
					directoryChooser.setTitle("选择文件夹");
					File dir = directoryChooser.showDialog(primaryStage);
					if (null != dir) {
						dirTextField.setText(dir.getAbsolutePath());
					}
				}

			}
		});

		downloadButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent actionEvent) {
				String downloadUrl = urlTextField.getText();
				String dir = dirTextField.getText();
				Image image = CommonUtility.getImage("title.png");
				if (null == downloadUrl || "".equals(downloadUrl.trim())) {
					Toast toast = new Toast("下载链接不能为空!");
					toast.showBottom(primaryStage);
				} else if (null == dir || "".equals(dir.trim())) {
					Toast toast = new Toast("保存路径不能为空!");
					toast.showBottom(primaryStage);
				} else if (null != downloadUrl && !downloadUrl.isEmpty() && null != dir && !dir.isEmpty()) {
					// 启动下载
					download(downloadUrl, dir);
				}
			}
		});
	}

	public boolean exist(String url) {
		ObservableList<TableItem> items = tableView.getItems();
		boolean flag = false;
		for (int i = 0, size = items.size(); i < size; i++) {
			String m3u8 = items.get(i).getM3u8();
			if (m3u8.equals(url)) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	public void download(String url, String dir) {
		if (exist(url)) {
			Toast toast = new Toast("已存在");
			Platform.runLater(new Runnable() {

				@Override
				public void run() {
					toast.showBottom(primaryStage);
				}
			});
		} else {
			TableItem tableItem = new TableItem(url, dir);
			tableView.getItems().add(tableItem);
			tableItem.getDownloadColumn().download();

		}
	}

	@Deprecated
	public void mark() {
		// 方式一最简单
//		Callback<TableColumn<EXTINF_TYPE, Boolean>, TableCell<EXTINF_TYPE, Boolean>> mergeCallback = CheckBoxTableCell
//				.forTableColumn(mergeColumn);
//		mergeColumn.setCellFactory(mergeCallback);
//		TableCell<EXTINF_TYPE, Boolean> mergeTableCell = mergeCallback.call(mergeColumn);
//		mergeTableCell.setItem(false);
		// 方式二
//		List<CheckBoxTableCell<EXTINF_TYPE, CheckBox>> mergeTableCells = new ArrayList<CheckBoxTableCell<EXTINF_TYPE, CheckBox>>();
//		Callback<TableColumn<EXTINF_TYPE, CheckBox>, TableCell<EXTINF_TYPE, CheckBox>> mergeCallback = new Callback<TableColumn<EXTINF_TYPE, CheckBox>, TableCell<EXTINF_TYPE, CheckBox>>() {
//
//			@Override
//			public TableCell<EXTINF_TYPE, CheckBox> call(TableColumn<EXTINF_TYPE, CheckBox> param) {
//				CheckBoxTableCell<EXTINF_TYPE, CheckBox> mergeTableCell = new CheckBoxTableCell<EXTINF_TYPE, CheckBox>();
//				mergeTableCells.add(mergeTableCell);
////				tableView.setEditable(true);
////				BooleanBinding not = Bindings.not(tableView.editableProperty().and(mergeColumn.editableProperty())
////						.and(mergeTableCell.editableProperty()));
////				System.out.println(tableView.editableProperty() + "," + mergeColumn.editableProperty() + ","
////						+ mergeTableCell.editableProperty());
//				return mergeTableCell;
//			}
//		};
//		mergeColumn.setCellFactory(mergeCallback);

		// 方式三
//		Callback<CellDataFeatures<EXTINF_TYPE, CheckBox>, ObservableValue<CheckBox>> mergeCallback = new Callback<TableColumn.CellDataFeatures<EXTINF_TYPE, CheckBox>, ObservableValue<CheckBox>>() {
//
//			@Override
//			public ObservableValue<CheckBox> call(CellDataFeatures<EXTINF_TYPE, CheckBox> param) {
//				CheckBoxProperty checkBoxProperty = new CheckBoxProperty();
//				return checkBoxProperty;
//			}
//		};
//		mergeColumn.setCellValueFactory(mergeCallback);
	}
}
