package application;

import java.io.File;

import application.component.DirTableCell;
import application.component.ProgressBarBox;
import application.dto.TableItem;
import application.utils.CommonUtility;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class MainView extends Application {
	private int width = 800;
	private int height = 500;
	private Stage primaryStage;
	private TextField urlTextField;
	private TextField dirTextField;
	private Button downloadButton;
	private TableView<TableItem> tableView;

	public static void main(String[] args) {
		Application.launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				System.exit(0);
			}
		});
		this.primaryStage = primaryStage;
		primaryStage.setTitle("下载m3u8视频");
		ObservableList<javafx.scene.image.Image> icons = primaryStage.getIcons();
		icons.add(CommonUtility.getImage("title.png"));

		Pane leftRegion = new Pane();
		leftRegion.setPrefWidth(width * 0.2);
		leftRegion.setMinWidth(width * 0.2);
		leftRegion.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, null, null)));

		BorderPane rightBox = new BorderPane();
		rightBox.setPrefWidth(width * 0.8);
		rightBox.setMinWidth(width * 0.8);
		BorderPane root = new BorderPane();

		GridPane topGridPane = new GridPane();
		topGridPane.setVgap(5);
//		topGridPane.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		rightBox.setTop(topGridPane);

		Label urlLabel = new Label("下载链接");
		GridPane.setHalignment(urlLabel, HPos.RIGHT);
		GridPane.setMargin(urlLabel, new Insets(5, 5, 5, 5));
		topGridPane.add(urlLabel, 0, 0);

		urlTextField = new TextField();
		urlTextField.setPrefWidth(350);
		GridPane.setMargin(urlTextField, new Insets(5, 5, 5, 0));
		GridPane.setHalignment(urlTextField, HPos.LEFT);
		topGridPane.add(urlTextField, 1, 0);

		Label dirLabel = new Label("保存路径");
		GridPane.setMargin(dirLabel, new Insets(0, 5, 5, 5));
		GridPane.setHalignment(dirLabel, HPos.RIGHT);
		topGridPane.add(dirLabel, 0, 1);

		dirTextField = new TextField();
		dirTextField.setPrefWidth(310);
		GridPane.setMargin(dirTextField, new Insets(0, 5, 5, 0));
		dirTextField.setTooltip(new Tooltip("双击、或按Enter键选择目录"));
		topGridPane.add(dirTextField, 1, 1);

		downloadButton = new Button("下载");
		downloadButton.setPrefHeight(60);
		downloadButton.setPrefWidth(60);
		GridPane.setMargin(downloadButton, new Insets(0, 0, 0, 10));
		topGridPane.add(downloadButton, 2, 0, 1, 2);

		setOnAction();
		initTableView();
		rightBox.setCenter(tableView);

		Scene scene = new Scene(root, width, height, Color.WHITE);
		root.setLeft(leftRegion);
		root.setCenter(rightBox);
//		root.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void initTableView() {
		// https://blog.csdn.net/servermanage/article/details/102317726
		// https://blog.csdn.net/MrChung2016/article/details/71774496
		// https://docs.oracle.com/javafx/2/ui_controls/table-view.htm#CJABIEED
		// http://www.javafxchina.net/blog/2015/04/doc03_tableview/
		tableView = new TableView<TableItem>();
		// 可自由设置显示列
//		tableView.setTableMenuButtonVisible(true);
		tableView.setEditable(true);
		// 自动拉伸列，是所有的列占满整个表格
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		Label label = new Label("快来下载吧!!!");
		label.setFont(new Font(30));
		tableView.setPlaceholder(label);

//		tableView.setRowFactory(new Callback<TableView<TableItem>, TableRow<TableItem>>() {
//
//			@Override
//			public TableRow<TableItem> call(TableView<TableItem> param) {
//				ExtTableRow extTableRow = new ExtTableRow();
//				return extTableRow;
//			}
//		});
//		Callback<TableView<TableItem>, TableRow<TableItem>> rowFactory = tableView.getRowFactory();
		TableViewSelectionModel<TableItem> selectionModel = tableView.getSelectionModel();
//		selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
		// set selection mode to only 1 row
//		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		// set selection mode to multiple rows
//		selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
//		ObservableList<EXTINF> selectedItems = selectionModel.getSelectedItems();
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

						if (!empty) {
							int rowIndex = this.getIndex() + 1;
							this.setText(String.valueOf(rowIndex));
						}
					}
				};
				return tableCell;
			}
		});

		TableColumn<TableItem, ProgressBarBox> progressColumn = new TableColumn<TableItem, ProgressBarBox>("已下载");
		progressColumn.setPrefWidth(208);
		progressColumn.setMinWidth(208);
		progressColumn.setMaxWidth(208);
		progressColumn.setCellValueFactory(new PropertyValueFactory<TableItem, ProgressBarBox>("progressBarBox"));

		TableColumn<TableItem, ProgressBarBox> fileSizeColumn = new TableColumn<TableItem, ProgressBarBox>("文件大小");
		fileSizeColumn.setPrefWidth(78);
		fileSizeColumn.setMinWidth(78);
		fileSizeColumn.setMaxWidth(78);
		fileSizeColumn.setCellValueFactory(new PropertyValueFactory<TableItem, ProgressBarBox>("fileSizeBox"));

		TableColumn<TableItem, String> dirColumn = new TableColumn<TableItem, String>("保存路径");
		dirColumn.setMinWidth(40);
		dirColumn.setCellFactory(new Callback<TableColumn<TableItem, String>, TableCell<TableItem, String>>() {

			@Override
			public TableCell<TableItem, String> call(TableColumn<TableItem, String> param) {
				DirTableCell tableCell = new DirTableCell();
				return tableCell;
			}
		});
		dirColumn.setCellValueFactory(new PropertyValueFactory<TableItem, String>("dir"));

		TableColumn<TableItem, CheckBox> mergeColumn = new TableColumn<TableItem, CheckBox>("合并");
		mergeColumn.setSortable(false);
		mergeColumn.setPrefWidth(30);
		mergeColumn.setMinWidth(30);
		mergeColumn.setMaxWidth(30);
		mergeColumn.setStyle("-fx-alignment:center;");
		mergeColumn.setCellValueFactory(new PropertyValueFactory<TableItem, CheckBox>("mergeCheckBox"));

		TableColumn<TableItem, Button> mergeOptColumn = new TableColumn<TableItem, Button>("操作");
		mergeOptColumn.setSortable(false);
		mergeOptColumn.setPrefWidth(46);
		mergeOptColumn.setMinWidth(46);
		mergeOptColumn.setMaxWidth(46);
//		optColumn.setCellFactory((callback) -> {
//			ButtonTableCell cell = new ButtonTableCell();
//			return cell;
//		});
//		optColumn.setCellFactory(new Callback<TableColumn<EXTINF, Button>, TableCell<EXTINF, Button>>() {
//			private int index = 0;
//
//			@Override
//			public TableCell<EXTINF, Button> call(TableColumn<EXTINF, Button> param) {
//				ButtonTableCell tableCell = new ButtonTableCell(tableView, optColumn, "合并" + index);
//				index++;
////				tableCell.addCell(mergeTableCells);
//				return tableCell;
//			}
//		});
		mergeOptColumn.setCellValueFactory(new PropertyValueFactory<TableItem, Button>("mergeButton"));

		tableView.getColumns().addAll(indexColumn, progressColumn, fileSizeColumn, dirColumn, mergeColumn,
				mergeOptColumn);

//		new MapValueFactory<T>(key);

//		ArrayList<EXTINF> arrayList = new ArrayList<EXTINF>();
//		for (int i = 0; i < 10; i++) {
//			EXTINF progressContainer = new EXTINF();
//			progressContainer.setDir("dir" + i);
//			arrayList.add(progressContainer);
//		}
//		ObservableList<EXTINF> observableArrayList = FXCollections.observableArrayList(arrayList);
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
					tableItem.getProgressBarBox().download();
				}
			}
		});
	}

	public void m() {
		// 方式一最简单
//		Callback<TableColumn<EXTINF, Boolean>, TableCell<EXTINF, Boolean>> mergeCallback = CheckBoxTableCell
//				.forTableColumn(mergeColumn);
//		mergeColumn.setCellFactory(mergeCallback);
//		TableCell<EXTINF, Boolean> mergeTableCell = mergeCallback.call(mergeColumn);
//		mergeTableCell.setItem(false);
		// 方式二
//		List<CheckBoxTableCell<EXTINF, CheckBox>> mergeTableCells = new ArrayList<CheckBoxTableCell<EXTINF, CheckBox>>();
//		Callback<TableColumn<EXTINF, CheckBox>, TableCell<EXTINF, CheckBox>> mergeCallback = new Callback<TableColumn<EXTINF, CheckBox>, TableCell<EXTINF, CheckBox>>() {
//
//			@Override
//			public TableCell<EXTINF, CheckBox> call(TableColumn<EXTINF, CheckBox> param) {
//				CheckBoxTableCell<EXTINF, CheckBox> mergeTableCell = new CheckBoxTableCell<EXTINF, CheckBox>();
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
//		Callback<CellDataFeatures<EXTINF, CheckBox>, ObservableValue<CheckBox>> mergeCallback = new Callback<TableColumn.CellDataFeatures<EXTINF, CheckBox>, ObservableValue<CheckBox>>() {
//
//			@Override
//			public ObservableValue<CheckBox> call(CellDataFeatures<EXTINF, CheckBox> param) {
//				CheckBoxProperty checkBoxProperty = new CheckBoxProperty();
//				return checkBoxProperty;
//			}
//		};
//		mergeColumn.setCellValueFactory(mergeCallback);
	}
}
