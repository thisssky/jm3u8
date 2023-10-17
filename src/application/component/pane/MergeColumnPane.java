package application.component.pane;

import application.component.task.MergeTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class MergeColumnPane extends AnchorPane {
	private String dir;

	private GridPane container;
	private ProgressBar progressBar;
	private Label label;
	private Service<Integer> service;

	public MergeColumnPane(String dir) {
		this.dir = dir;

		progressBar = new ProgressBar();
		progressBar.setPrefHeight(22);
		progressBar.setPrefWidth(70);
		progressBar.setProgress(0);
		label = new Label("0MB");
		label.setFont(Font.font(16));
		label.setPrefWidth(70);
		label.setTextFill(Color.BLACK);// web("#0076a3"));
		label.setStyle("-fx-alignment:center;");

		container = new GridPane();
		AnchorPane.setTopAnchor(container, 2D);
		AnchorPane.setLeftAnchor(container, 1D);
		container.add(progressBar, 0, 0);
		container.add(label, 0, 0);
		getChildren().add(container);
	}

	/** 合并 */
	public void merge() {
		// 让进度条动起来
		progressBar.setProgress(-1);
		service = new Service<Integer>() {

			@Override
			protected Task<Integer> createTask() {
				return new MergeTask(dir);
			}
		};
		service.messageProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				double l = Double.valueOf(newValue);
				String text = "";
				if (l / 1024 < 1) {
					text = (int) l + "KB";
				} else if (l / 1024 > 1 && l / (1024 * 1024) < 1) {
					text = (int) l / 1024 + "MB";
				} else if (l / (1024 * 1024) > 1 && l / (1024 * 1024 * 1024) < 1) {
					text = String.format("%.1fGB", l / (1024 * 1024));
				}

				label.setText(text);

			}

		});
		// 获取进度条最终状态
		progressBar.progressProperty().bind(service.progressProperty());
		service.start();
	}

}
