package application.component;

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Toast {

	private String msg;
	private long milliseconds = 1000;
	private Stage stage;
	private Label label;

	public Toast(String msg) {
		this(msg, 1000);
	}

	public Toast(String msg, long milliseconds) {
		this.msg = msg;
		this.milliseconds = milliseconds;
		label = new Label();
		stage = new Stage();
		//透明
		stage.initStyle(StageStyle.TRANSPARENT);

	}

	public static void main(String[] args) {
	}

	public void show() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				Platform.runLater(() -> stage.close());
			}
		};
		Timer timer = new Timer();
		timer.schedule(task, milliseconds);

		// label透明,圆角
		label.setStyle("-fx-background: rgba(56,56,56,0.7);-fx-border-radius: 25;-fx-background-radius: 25");
		label.setTextFill(Color.rgb(225, 255, 226));// 消息字体颜色
		label.setPrefHeight(50);
		label.setPadding(new Insets(15));
		label.setAlignment(Pos.CENTER);// 居中
		label.setFont(new Font(20));// 字体大小
		label.setText(msg);
		Scene scene = new Scene(label);
		scene.setFill(null);// 场景透明
		stage.setScene(scene);
		stage.show();
		//TODO
		//动画效果，位置
	}

}
