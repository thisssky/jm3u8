package application;

import java.io.File;
import java.util.ArrayList;

import application.component.ButtonTableCell;
import application.dto.EXTINF;
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
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
	private TableView<EXTINF> tableView;

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
		splitPane.setDividerPositions(0.3, 0.7);

		Scene scene = new Scene(splitPane, 800, 500, Color.WHITE);
		primaryStage.setResizable(false);

		VBox root = new VBox();
		splitPane.getItems().addAll(leftRegion, root);
//		root.setBackground(new Background(new BackgroundFill(Color.DARKGRAY, null, null)));
		GridPane pane = new GridPane();
//		pane.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
		VBox.setMargin(pane, new Insets(10, 50, 0, 50));
		root.getChildren().add(pane);
		pane.setVgap(5);

		Label urlLabel = new Label("下载链接");
		urlTextField = new TextField();
		urlTextField.setPrefWidth(350);
		Label dirLabel = new Label("保存路径");

		dirTextField = new TextField();
		dirTextField.setPrefWidth(310);
		dirTextField.setTooltip(new Tooltip("双击、或按Enter键选择目录"));
		downloadButton = new Button("下载");
		downloadButton.setPrefWidth(40);
		HBox dirHBox = new HBox(dirTextField, downloadButton);
		initEventHandler();

		GridPane.setHalignment(urlLabel, HPos.RIGHT);
		pane.add(urlLabel, 0, 0);
		GridPane.setHalignment(urlTextField, HPos.LEFT);
		pane.add(urlTextField, 1, 0);

		GridPane.setHalignment(dirLabel, HPos.RIGHT);
		pane.add(dirLabel, 0, 1);
		pane.add(dirHBox, 1, 1);

		tableView = new TableView<EXTINF>();
		Label label = new Label("快来下载吧!!!");
		label.setFont(new Font(30));
		tableView.setPlaceholder(label);
		//自动拉伸列，是所有的列占满整个表格
		tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		TableViewSelectionModel<EXTINF> selectionModel = tableView.getSelectionModel();
		// set selection mode to only 1 row
//		selectionModel.setSelectionMode(SelectionMode.SINGLE);
		// set selection mode to multiple rows
//		selectionModel.setSelectionMode(SelectionMode.MULTIPLE);
//		ObservableList<EXTINF> selectedItems = selectionModel.getSelectedItems();
//		selectionModel.clearSelection();

		TableColumn<EXTINF, String> indexColumn = new TableColumn<EXTINF, String>("#");
		indexColumn.setSortable(true);
		indexColumn.setCellFactory((col) -> {
			TableCell<EXTINF, String> cell = new TableCell<EXTINF, String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);

					if (!empty) {
						int rowIndex = this.getIndex() + 1;
						this.setText(String.valueOf(rowIndex));
					}
				}
			};
			return cell;
		});
		TableColumn<EXTINF, String> nameColumn = new TableColumn<EXTINF, String>("名称");
		TableColumn<EXTINF, String> progressColumn = new TableColumn<EXTINF, String>("已完成");
		TableColumn<EXTINF, String> dirColumn = new TableColumn<EXTINF, String>("保存路径");
		TableColumn<EXTINF, CheckBox> mergeColumn = new TableColumn<EXTINF, CheckBox>("合并");
		mergeColumn.setSortable(false);
		mergeColumn.setPrefWidth(28);
		mergeColumn.setMaxWidth(28);
		mergeColumn.setCellFactory(new Callback<TableColumn<EXTINF, CheckBox>, TableCell<EXTINF, CheckBox>>() {
			
			@Override
			public TableCell<EXTINF, CheckBox> call(TableColumn<EXTINF, CheckBox> param) {
				CheckBoxTableCell<EXTINF, CheckBox> checkBoxTableCell = new CheckBoxTableCell<EXTINF, CheckBox>();
				return checkBoxTableCell;
			}
		});
		TableColumn<EXTINF, Button> optColumn = new TableColumn<EXTINF, Button>("操作");
		optColumn.setSortable(false);
//		optColumn.setCellFactory((callback) -> {
//			ButtonTableCell cell = new ButtonTableCell();
//			return cell;
//		});
		optColumn.setCellFactory(new Callback<TableColumn<EXTINF, Button>, TableCell<EXTINF, Button>>() {

			@Override
			public TableCell<EXTINF, Button> call(TableColumn<EXTINF, Button> param) {
				ButtonTableCell tableCell = new ButtonTableCell("合并");
				return tableCell;
			}
		});
		dirColumn.setCellValueFactory(new PropertyValueFactory<EXTINF, String>("dir"));
//		
		// https://blog.csdn.net/servermanage/article/details/102317726
		// https://blog.csdn.net/MrChung2016/article/details/71774496

		tableView.getColumns().addAll(indexColumn, nameColumn, progressColumn, dirColumn, mergeColumn, optColumn);
		ArrayList<EXTINF> arrayList = new ArrayList<EXTINF>();
		for (int i = 0; i < 10; i++) {
			EXTINF progressContainer = new EXTINF();
			progressContainer.setDir("dir" + i);
			arrayList.add(progressContainer);
		}

		ObservableList<EXTINF> observableArrayList = FXCollections.observableArrayList(arrayList);
		tableView.setItems(observableArrayList);
		pane.add(tableView, 0, 2, 2, 1);

		primaryStage.setScene(scene);
		primaryStage.show();

	}

	public void showStuTable(ObservableList<EXTINF> stuLists) {
		TableColumn<EXTINF, String> idCol = new TableColumn<EXTINF, String>();
		idCol.setCellFactory((col) -> {
			TableCell<EXTINF, String> cell = new TableCell<EXTINF, String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					this.setText(null);
					this.setGraphic(null);

					if (!empty) {
						int rowIndex = this.getIndex() + 1;
						this.setText(String.valueOf(rowIndex));
					}
				}
			};
			return cell;
		});
	}

	private void initEventHandler() {
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
				EXTINF extinf = new EXTINF();
				extinf.setDir("xxxxxxxxxxx");
				tableView.getItems().add(extinf);

				if (null != downloadUrl && !downloadUrl.isEmpty() && null != dir && !dir.isEmpty()) {
					// 启动下载
//				ProgressContainer progressContainer = new ProgressContainer(downloadUrl, dir);
//				addProgressContainer(progressContainer);
//					progressContainer.download();
				}
			}
		});
	}

}
