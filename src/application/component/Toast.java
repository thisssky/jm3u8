package application.component;

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;

public class Toast extends Popup {

	private Label label;

	public Toast(String msg) {
		this(msg, 1000);
	}

	public double labelWidth;

	public Toast(String msg, long milliseconds) {
		sizeToScene();
		label = new Label(msg);
		label.setFont(new Font(18));// 字体大小
		Text theText = new Text(msg);
		theText.setFont(label.getFont());
		labelWidth = theText.getBoundsInLocal().getWidth();
		label.setPrefHeight(36);
		label.setAlignment(Pos.CENTER);// 居中
		BorderPane borderPane = new BorderPane(label);
		BorderPane.setMargin(label, new Insets(0, 18, 0, 18));
		borderPane.setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(18), null)));
		getScene().setRoot(borderPane);
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

	public void showBottom(Window primaryStage) {
		double px = primaryStage.getX() + primaryStage.getWidth() / 2 - (labelWidth + 36) / 2;
		double py = primaryStage.getY() + primaryStage.getHeight() - 36 - 20;
		show(primaryStage, px, py);
	}
}
