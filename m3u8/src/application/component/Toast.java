package application.component;

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Popup;

public class Toast extends Popup {

	private Label label;

	public Toast(String msg) {
		this(msg, 1000);
	}

	public Toast(String msg, long milliseconds) {
		sizeToScene();
		label = new Label(msg);
		label.setPrefWidth(100);
		label.setPrefHeight(36);
		// label背景色,圆角
		label.setBackground(
				new Background(new BackgroundFill(Color.DARKGRAY, new CornerRadii(18), new Insets(0, 0, 0, 0))));
		label.setAlignment(Pos.CENTER);// 居中
		label.setFont(new Font(18));// 字体大小
		getScene().setRoot(label);
//		ObservableList<Node> content = getContent();
//		content.add(label);
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> hide());
				timer.cancel();
			}
		};
		timer.schedule(task, milliseconds);
	}

}
