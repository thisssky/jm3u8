package application.testexample;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * @author stone
 */

public class ThreadTask extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.setTitle("Hello World!");

		Button startBtn = new Button("开始");
		Button cancelBtn = new Button("取消");
//		Button resetBtn = new Button("重置");
//		Button restartBtn = new Button("重启");
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
//		root.add(resetBtn, 2, 0);
//		root.add(restartBtn, 3, 0);
		root.add(progressBar, 2, 0);

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
		ExtTask myTask = new ExtTask();
		Thread thread = new Thread(myTask);

		startBtn.setOnAction(event -> thread.start());
		cancelBtn.setOnAction(event -> myTask.cancel());

		myTask.progressProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				progressBar.setProgress(newValue.doubleValue());
				System.out.println("ob:" + observable.getValue().doubleValue() + ",old:" + oldValue.doubleValue()
						+ ",new:" + newValue.doubleValue());
				// 去更新label值
				valueValue.setText(String.format("%.2f%%", newValue.doubleValue() * 100));
			}
		});

		// wordkDone指的是进度
//		myTask.stateProperty().addListener(new ChangeListener<Worker.State>() {
//			@Override
//			public void changed(ObservableValue<? extends Worker.State> observable, Worker.State oldValue,
//					Worker.State newValue) {
//				stateLabel.setText(newValue.toString());
//			}
//		});

//		myTask.titleProperty().addListener(new ChangeListener<String>() {
//			@Override
//			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//				titleLabel.setText(newValue);
//			}
//		});
//		myTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
//
//			@Override
//			public void handle(WorkerStateEvent event) {
//				System.err.println("succeeded");
//			}
//		});

//		myTask.valueProperty().addListener(new ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//				if (newValue.doubleValue() == 1) {
//					valueLabel.setText("完成");
//				}
//			}
//		});

//		myTask.messageProperty().addListener(new ChangeListener<String>() {
//			@Override
//			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//				messageLabel.setText(newValue);
//			}
//		});

		// 异常监听 监听现在状态是否有异常并打印
//		myTask.exceptionProperty().addListener(new ChangeListener<Throwable>() {
//			@Override
//			public void changed(ObservableValue<? extends Throwable> observable, Throwable oldValue,
//					Throwable newValue) {
//				System.out.println(newValue);
//			}
//		});

	}

	public static void main(String[] args) {
		launch(args);
	}
}

class ExtTask extends Task<Number> {

	public ExtTask() {
		super();
	}

	@Override
	protected Number call() throws Exception {

//		this.updateTitle("copy");

		FileInputStream fis = new FileInputStream(new File("C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\44\\out.mp4"));
		FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\44\\1\\out.mp4"));
		// 获取字节长
		double max = fis.available();
		byte[] readbyte = new byte[10000];
		int i = 0;
		// 目前完成进度
		double sum = 0;
		// 进度
		double progress = 0;
		while ((i = fis.read(readbyte, 0, readbyte.length)) != -1) {

			/*
			 * if (this.isCancelled()){ break; }
			 */
			fos.write(readbyte, 0, i);
			sum = sum + i;
			// 当前和总共
			this.updateProgress(sum, max);
			progress = sum / max;
//			System.out.println(progress);
			Thread.sleep(1);
			if (progress < 0.5) {
				this.updateMessage("请耐心等待");
			} else if (progress < 0.8) {
				this.updateMessage("马上就好");
			} else if (progress < 1) {
				this.updateMessage("即将完成");
			} else if (progress >= 1) {
				this.updateMessage("搞定了");
			}
		}

		fis.close();
		fos.close();
//		System.out.println(Platform.isFxApplicationThread());
		return progress;
	}
}
