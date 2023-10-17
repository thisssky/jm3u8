package application.testexample;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * @author stone
 */

public class ScheduledServiceTask extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Hello World!");

		Button startBtn = new Button("开始");
		Button cancelBtn = new Button("取消");
		Button resetBtn = new Button("重置");
		Button restartBtn = new Button("重启");
		ProgressBar progressBar = new ProgressBar(0);
		progressBar.setPrefWidth(200);

		Label stateLabel = new Label("state");
		Label stateValue = new Label("sValue");
		Label valueLabel = new Label("value");
		Label valueValue = new Label("vValue");
		Label titleLabel = new Label("title");
		Label titleValue = new Label("tValue");
		Label messageLabel = new Label("message");
		Label messageValue = new Label("mValue");

		GridPane root = new GridPane();
		root.setHgap(5);
		root.setVgap(5);
		root.add(startBtn, 0, 0);
		root.add(cancelBtn, 1, 0);
		root.add(resetBtn, 2, 0);
		root.add(restartBtn, 3, 0);
		root.add(progressBar, 4, 0);

		root.add(stateLabel, 0, 1);
		root.add(valueLabel, 1, 1);
		root.add(titleLabel, 2, 1);
		root.add(messageLabel, 3, 1);

		root.add(stateValue, 0, 2);
		root.add(valueValue, 1, 2);
		root.add(titleValue, 2, 2);
		root.add(messageValue, 3, 2);

		primaryStage.setScene(new Scene(root, 700, 300));
		primaryStage.show();

		//
		ExtScheduledService extScheduledService = new ExtScheduledService();
		// 等待5s开始、
		extScheduledService.setDelay(Duration.seconds(5));
		// 程序执行时间
		extScheduledService.setPeriod(Duration.seconds(100));
		// 启动失败重新启动
		extScheduledService.setRestartOnFailure(true);
		// 程序启动失败后重新启动次数
		extScheduledService.setMaximumFailureCount(4);

		startBtn.setOnAction(event -> {
			extScheduledService.start();
			System.out.println("开始");
		});
		cancelBtn.setOnAction(event -> {
			extScheduledService.cancel();
			System.out.println("取消");
		});
		resetBtn.setOnAction(event -> {
			extScheduledService.reset();
			System.out.println("重置");
		});
		restartBtn.setOnAction(event -> {
			extScheduledService.restart();
			System.out.println("重启");
		});

		extScheduledService.progressProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				progressBar.setProgress(newValue.doubleValue());
			}
		});

//		extScheduledService.valueProperty().addListener(new ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//				if (newValue != null) {
//					valueValue.setText(String.valueOf(newValue));
//				}
//			}
//		});
//
//		extScheduledService.lastValueProperty().addListener(new ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//				if (newValue != null) {
//					System.out.println("lastValue=" + newValue.intValue());
//				}
//			}
//		});

	}

	public static void main(String[] args) {
		launch(args);
	}
}

class ExtSSS extends ScheduledService<Number> {

	@Override
	protected Task<Number> createTask() {
		Task<Number> task = new Task<Number>() {

			@Override
			protected Number call() throws Exception {

				return null;
			}
		};
		return task;
	}

}

class ExtScheduledService extends ScheduledService<Number> {

	int sum = 0;

	@Override
	protected Task<Number> createTask() {

		Task<Number> task = new Task<Number>() {

//			@Override
//			protected void updateValue(Number value) {
//				super.updateValue(value);
//				if (value.intValue() == 10) {
//					ExtScheduledService.this.cancel();
//					System.out.println("任务取消");
//				}
//			}

			@Override
			protected Number call() throws Exception {
				sum += 0.01;
				updateProgress(sum, 1);
				return sum;
			}
		};
		return task;
	}

}
