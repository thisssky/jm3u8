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

public class Toast2 extends Popup {

	private String msg;
	private long milliseconds = 1000;
	private Label label;

	public Toast2(String msg) {
		this(msg, 1000);
	}

	public Toast2(String msg, long milliseconds) {
		this.msg = msg;
		this.milliseconds = milliseconds;
		label = new Label(msg);

		// label透明,圆角
		label.setBackground(
				new Background(new BackgroundFill(Color.DARKGRAY, new CornerRadii(25), new Insets(0, 5, 0, 5))));
//		label.setTextFill(Color.rgb(225, 255, 226));// 消息字体颜色
		label.setPrefHeight(50);
		label.setAlignment(Pos.CENTER);// 居中
		label.setFont(new Font(18));// 字体大小
		getScene().setRoot(label);
//		ObservableList<Node> content = getContent();
//		content.add(label);

		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> hide());
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, milliseconds);
	}

}
