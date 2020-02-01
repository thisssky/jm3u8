package application;

import java.io.File;
import java.util.ArrayList;

import application.component.ProgressBarBox;
import application.dto.EXTINF;
import application.dto.TableItem;
import application.utils.CommonUtility;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.MapValueFactory;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class MainView extends Application {
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

		Region leftRegion = new Region();
		SplitPane splitPane = new SplitPane();
		splitPane.setDividerPositions(0.2, 0.8);

		Scene scene = new Scene(splitPane, 800, 500, Color.WHITE);
//		primaryStage.setResizable(false);

		VBox rightBox = new VBox();
		splitPane.getItems().addAll(leftRegion, rightBox);
//		root.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));
		GridPane topGridPane = new GridPane();
//		topGridPane.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		VBox.setMargin(topGridPane, new Insets(10, 10, 10, 10));
		rightBox.getChildren().add(topGridPane);
		topGridPane.setVgap(5);

		Label urlLabel = new Label("下载链接");
		GridPane.setHalignment(urlLabel, HPos.RIGHT);
		topGridPane.add(urlLabel, 0, 0);

		urlTextField = new TextField();
		urlTextField.setPrefWidth(350);
		GridPane.setHalignment(urlTextField, HPos.LEFT);
		topGridPane.add(urlTextField, 1, 0);

		Label dirLabel = new Label("保存路径");
		GridPane.setHalignment(dirLabel, HPos.RIGHT);
		topGridPane.add(dirLabel, 0, 1);

		dirTextField = new TextField();
		dirTextField.setPrefWidth(310);
		dirTextField.setTooltip(new Tooltip("双击、或按Enter键选择目录"));
		topGridPane.add(dirTextField, 1, 1);

		downloadButton = new Button("下载");
		downloadButton.setPrefHeight(60);
		downloadButton.setPrefWidth(60);
		GridPane.setMargin(downloadButton, new Insets(0, 0, 0, 10));
		topGridPane.add(downloadButton, 2, 0, 1, 2);

		setOnAction();
		initTableView();
		rightBox.getChildren().add(tableView);

		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void initTableView() {
		// https://blog.csdn.net/servermanage/article/details/102317726
		// https://blog.csdn.net/MrChung2016/article/details/71774496
		// https://docs.oracle.com/javafx/2/ui_controls/table-view.htm#CJABIEED
		// http://www.javafxchina.net/blog/2015/04/doc03_tableview/
		tableView = new TableView<TableItem>();
//		GridPane.setHgrow(tableView, Priority.ALWAYS);
		tableView.setEditable(true);
		// 自动拉伸列，是所有的列占满整个表格
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		Label label = new Label("快来下载吧!!!");
		label.setFont(new Font(30));
		tableView.setPlaceholder(label);
		TableViewSelectionModel<TableItem> selectionModel = tableView.getSelectionModel();
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
//							System.out.println("index:" + getIndex());
						}
					}
				};
				return tableCell;
			}
		});

		TableColumn<TableItem, ProgressBarBox> progressColumn = new TableColumn<TableItem, ProgressBarBox>("已完成");
		progressColumn.setPrefWidth(208);
		progressColumn.setMinWidth(208);
		progressColumn.setMaxWidth(208);
		progressColumn.setCellValueFactory(new PropertyValueFactory<TableItem, ProgressBarBox>("progressBarBox"));

		TableColumn<TableItem, String> nameColumn = new TableColumn<TableItem, String>("名称");
		nameColumn.setResizable(true);
		nameColumn.setMinWidth(40);

		TableColumn<TableItem, String> dirColumn = new TableColumn<TableItem, String>("保存路径");
		dirColumn.setResizable(true);
		dirColumn.setMinWidth(40);
		dirColumn.setCellValueFactory(new PropertyValueFactory<TableItem, String>("dir"));

		TableColumn<TableItem, CheckBox> mergeColumn = new TableColumn<TableItem, CheckBox>("合并");
		mergeColumn.setSortable(false);
		mergeColumn.setPrefWidth(30);
		mergeColumn.setMinWidth(30);
		mergeColumn.setMaxWidth(30);
		mergeColumn.setStyle("-fx-alignment:center;");
		mergeColumn.setCellValueFactory(new PropertyValueFactory<TableItem, CheckBox>("mergeCheckBox"));

		TableColumn<TableItem, Button> optColumn = new TableColumn<TableItem, Button>("操作");
		optColumn.setSortable(false);
		optColumn.setPrefWidth(46);
		optColumn.setMinWidth(46);
		optColumn.setMaxWidth(46);
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
		optColumn.setCellValueFactory(new PropertyValueFactory<TableItem, Button>("mergeButton"));

		dirColumn.setCellValueFactory(new PropertyValueFactory<TableItem, String>("dir"));
		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuItem = new MenuItem("文件夹");
		contextMenu.getItems().add(menuItem);
		dirColumn.setContextMenu(contextMenu);

		tableView.getColumns().addAll(indexColumn, nameColumn, progressColumn, dirColumn, mergeColumn, optColumn);

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
//				if (null == downloadUrl || "".equals(downloadUrl.trim())) {
//					Alert urlAlert = new Alert(AlertType.ERROR);
//					Stage window = (Stage) urlAlert.getDialogPane().getScene().getWindow();
//					window.getIcons().add(image);
//					urlAlert.setHeaderText("下载链接不能为空!");
//					urlAlert.setTitle("提示");
//					urlAlert.show();
//				} else if (null == dir || "".equals(dir.trim())) {
//					Alert urlAlert = new Alert(AlertType.ERROR);
//					Stage window = (Stage) urlAlert.getDialogPane().getScene().getWindow();
//					window.getIcons().add(image);
//					urlAlert.setHeaderText("保存路径不能为空!");
//					urlAlert.setTitle("提示");
//					urlAlert.show();
//				}

				TableItem tableItem = new TableItem(downloadUrl, dir);
				tableView.getItems().add(tableItem);
				tableItem.getProgressBarBox().download();
				if (null != downloadUrl && !downloadUrl.isEmpty() && null != dir && !dir.isEmpty()) {
					// 启动下载
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
