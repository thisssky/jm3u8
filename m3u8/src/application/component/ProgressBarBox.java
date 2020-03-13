package application.component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class ProgressBarBox extends AnchorPane {
	private String m3u8;
	private String dir;

	private GridPane container;
	private ProgressBar progressBar;
	private Label label;

	public ProgressBarBox(String dir) {
		this.dir = dir;

		progressBar = new ProgressBar();
		progressBar.focusTraversableProperty().get();
		progressBar.setPrefWidth(70);
		progressBar.setProgress(0);
		label = new Label();
		label.autosize();
		label.setPrefWidth(70);
		label.setText("1024MB");
		label.setTextFill(Color.BLACK);// web("#0076a3"));
		label.setStyle("-fx-alignment:center;");

		container = new GridPane();
		AnchorPane.setTopAnchor(container, 2D);
		AnchorPane.setLeftAnchor(container, 1D);
		container.add(progressBar, 0, 0);
		container.add(label, 0, 0);
		getChildren().add(container);
	}

	public ProgressBarBox(String m3u8, String dir) {
		this.m3u8 = m3u8;
		this.dir = dir;

		progressBar = new ProgressBar();
		progressBar.focusTraversableProperty().get();
		progressBar.setPrefWidth(200);
		progressBar.setProgress(0);
		label = new Label();
		label.autosize();
		label.setPrefWidth(200);
		label.setText("0.00%");
		label.setTextFill(Color.BLACK);// web("#0076a3"));
//		label.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
//		label.setTextAlignment(TextAlignment.CENTER);
		label.setStyle("-fx-alignment:center;");

		container = new GridPane();
		AnchorPane.setTopAnchor(container, 2D);
		AnchorPane.setLeftAnchor(container, 1D);
		container.add(progressBar, 0, 0);
		container.add(label, 0, 0);
		getChildren().add(container);

	}

	public void download() {
		Service<Integer> service = new Service<Integer>() {

			@Override
			protected Task<Integer> createTask() {
				ProgressBarTask tasks = new ProgressBarTask(m3u8, dir);
				return tasks;
			};
		};
		service.progressProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//				System.out.println("ob:" + observable.getValue().doubleValue() + ",old:" + oldValue.doubleValue()
//						+ ",new:" + newValue.doubleValue());
				progressBar.setProgress(newValue.doubleValue());
				// 去更新label值
				label.setText(String.format("%.2f%%", newValue.doubleValue() * 100));
			}
		});
		service.start();
	}

	public void merge() {
		// 让进度条动起来
		progressBar.setProgress(-1);
		Service<String> service = new Service<String>() {

			@Override
			protected Task<String> createTask() {
				return new FileSizeTask(dir);
			};
		};
		service.messageProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				label.setText(newValue);
			}
		});
		// 获取进度条最终状态
		progressBar.progressProperty().bind(service.progressProperty());
		service.start();
	}
}
