package application;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import application.component.ContextMenuTableRow;
import application.component.DirTableCell;
import application.component.DownloadColumn;
import application.component.MergeColumn;
import application.dto.TableItem;
import application.dto.XMLRoot;
import application.utils.CommonUtility;
import application.utils.JAXBUtils;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
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
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class App extends Application {
	private int width = 800;
	private int height = 500;
	private Stage primaryStage;
	private TextField urlTextField;
	private TextField dirTextField;
	private Button downloadButton;
	private TableView<TableItem> tableView;
	private BorderPane root;
	private BorderPane rightBox;

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
			}
		});
		this.primaryStage = primaryStage;
		width = CommonUtility.getDimensionWidth() / 2;
		height = CommonUtility.getDimensionHeight() / 2;
		primaryStage.setTitle("下载m3u8视频");
		ObservableList<javafx.scene.image.Image> icons = primaryStage.getIcons();
		icons.add(CommonUtility.getImage("title.png"));

		root = new BorderPane();

//		Pane leftRegion = new Pane();
//		leftRegion.setPrefWidth(width * 0.2);
//		leftRegion.setMinWidth(width * 0.2);
//		leftRegion.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));
//		root.setLeft(leftRegion);

		rightBox = new BorderPane();
		root.setCenter(rightBox);

		initTop();
		initTableView();
		setOnAction();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void initTop() {
		GridPane topGridPane = new GridPane();
		topGridPane.setVgap(5);
		rightBox.setTop(topGridPane);

		Label urlLabel = new Label("下载链接");
		urlLabel.setPrefWidth(52);
		urlLabel.setMinWidth(52);
		GridPane.setHalignment(urlLabel, HPos.RIGHT);
		GridPane.setMargin(urlLabel, new Insets(5, 0, 0, 5));
		topGridPane.add(urlLabel, 0, 0);

		urlTextField = new TextField();
		urlTextField.setPrefWidth(350);
		urlTextField.setPrefHeight(25);
		GridPane.setMargin(urlTextField, new Insets(5, 5, 0, 5));
		GridPane.setConstraints(urlTextField, 1, 0, 1, 1, HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER);
		topGridPane.add(urlTextField, 1, 0);

		Label dirLabel = new Label("保存路径");
		GridPane.setMargin(dirLabel, new Insets(5, 0, 5, 5));
		GridPane.setHalignment(dirLabel, HPos.RIGHT);
		topGridPane.add(dirLabel, 0, 1);

		dirTextField = new TextField();
		dirTextField.setPrefWidth(310);
		dirTextField.setPrefHeight(25);
		GridPane.setMargin(dirTextField, new Insets(5, 5, 5, 5));
		Tooltip tooltip = new Tooltip("双击、或按Enter键选择目录");
		tooltip.setFont(Font.font(18));
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
					CommonUtility.alert("请选择保存路径!", AlertType.ERROR);
				} else {
					BufferedReader bufferedReader = null;
					try {
						for (int i = 0; i < files.size(); i++) {
							File file = files.get(i);
							if (file.getName().equals("m3u8.txt")) {
								FileReader fileReader = new FileReader(file);
								bufferedReader = new BufferedReader(fileReader);
								Stream<String> lines = bufferedReader.lines();
								lines.forEach(item -> {
									System.out.println(item);

									String dir = dirTextField.getText() + File.separator + fileReader.hashCode() + "-"
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
								CommonUtility.alert("请使用m3u8.txt资源文件!", AlertType.ERROR);
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
								CommonUtility.alert("已存在!", AlertType.ERROR);
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
						CommonUtility.alert("文件格式不对!", AlertType.ERROR);
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
					Alert urlAlert = new Alert(AlertType.ERROR);
					Stage window = (Stage) urlAlert.getDialogPane().getScene().getWindow();
					window.getIcons().add(image);
					urlAlert.setHeaderText("下载链接不能为空!");
					urlAlert.setTitle("提示");
					urlAlert.show();
				} else if (null == dir || "".equals(dir.trim())) {
					Alert urlAlert = new Alert(AlertType.ERROR);
					Stage window = (Stage) urlAlert.getDialogPane().getScene().getWindow();
					window.getIcons().add(image);
					urlAlert.setHeaderText("保存路径不能为空!");
					urlAlert.setTitle("提示");
					urlAlert.show();
				} else if (null != downloadUrl && !downloadUrl.isEmpty() && null != dir && !dir.isEmpty()) {
					// 启动下载
					TableItem tableItem = new TableItem(downloadUrl, dir);
					tableView.getItems().add(tableItem);
					tableItem.getDownloadColumn().download();
				}
			}
		});
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
