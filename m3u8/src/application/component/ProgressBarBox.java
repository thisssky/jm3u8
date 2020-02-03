package application.component;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class ProgressBarBox extends Pane {

	private GridPane gridPane;
	private ProgressBar progressBar;
	private Label progressBarTextLabel;
	private Service<Integer> progressBarService;

	public ProgressBarBox(String m3u8, String dir) {
		progressBar = new ProgressBar();
		progressBar.focusTraversableProperty().get();
		progressBar.setPrefWidth(200);
		progressBar.setProgress(0);
		progressBarTextLabel = new Label();
		progressBarTextLabel.autosize();
		progressBarTextLabel.setPrefWidth(200);
		progressBarTextLabel.setText("0.00%");
		progressBarTextLabel.setTextFill(Color.BLACK);// web("#0076a3"));
//		progressBarTextLabel.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
//		progressBarTextLabel.setTextAlignment(TextAlignment.CENTER);
		progressBarTextLabel.setStyle("-fx-alignment:center;");

		gridPane = new GridPane();
		gridPane.add(progressBar, 0, 0);

		gridPane.add(progressBarTextLabel, 0, 0);

		getChildren().add(gridPane);

//		progressBarTextLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
//
//			@Override
//			public void handle(MouseEvent event) {
//				if (MouseButton.SECONDARY == event.getButton()) {
//					ContextMenu dirMenuItem = dirMenuItem();
//					progressBarTextLabel.setContextMenu(dirMenuItem);
//				}
//
//			}
//		});

		progressBarService = new Service<Integer>() {

			@Override
			protected Task<Integer> createTask() {
				ProgressBarTask tasks = new ProgressBarTask(m3u8, dir);
				return tasks;
			};
		};
		progressBarService.progressProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//				System.out.println("ob:" + observable.getValue().doubleValue() + ",old:" + oldValue.doubleValue()
//						+ ",new:" + newValue.doubleValue());
				progressBar.setProgress(newValue.doubleValue());
				// 去更新label值
				progressBarTextLabel.setText(String.format("%.2f%%", newValue.doubleValue() * 100));
			}
		});

	}

	public ContextMenu dirMenuItem() {
		ContextMenu contextMenu = new ContextMenu();
		MenuItem menuItem = new MenuItem("文件夾");
		menuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					Desktop.getDesktop().open(new File("C:\\Users\\kyh\\Desktop\\m3u8"));
				} catch (IOException e) {
				}

			}
		});
		contextMenu.getItems().addAll(menuItem);
		return contextMenu;
	}

	public void download() {
		progressBarService.start();
	}

}
