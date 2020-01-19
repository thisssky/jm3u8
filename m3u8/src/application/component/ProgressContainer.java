package application.component;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import application.runnable.DownloadRunnable;
import application.utils.CommonUtility;
import application.utils.FFMPEG;
import application.utils.M3U8;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ProgressContainer {

	private Label label;
	private GridPane gridPane;
	private ProgressBar progressBar;
	private Label progressBarTextLabel;
	private Button megerButton;
	private HBox hBox;
	private Service<Integer> progressBarService;
	private Service<String> progressBarTextService;
	private AtomicInteger atomicInteger = new AtomicInteger(0);
	private List<String> list;
	private String downloadUrl;
	private String dir;
	private int num=20;
	private Timer timer = new Timer();

private	ExecutorService es = Executors.newFixedThreadPool(num);


	public Label getLabel() {
		return label;
	}

	public HBox gethBox() {
		return hBox;
	}


	public ProgressContainer(String downloadUrl, String dir) {
		this.downloadUrl = downloadUrl;
		this.dir = dir;
		label = new Label("下载进度");
		progressBar = new ProgressBar();
		progressBar.setPrefWidth(310);
		progressBar.setProgress(0);
		progressBarTextLabel = new Label();
		progressBarTextLabel.setText("0.00%");

		megerButton = new Button("合并");

		gridPane = new GridPane();
		hBox = new HBox();
		gridPane.add(progressBar, 0, 0);
		GridPane.setHalignment(progressBarTextLabel, HPos.CENTER);

		gridPane.add(progressBarTextLabel, 0, 0);
		hBox.getChildren().add(gridPane);
		hBox.getChildren().add(megerButton);

	}

	public void download() {
		progressBarService = new Service<Integer>() {

			@Override
			protected Task<Integer> createTask() {
				Task<Integer> task = new Task<Integer>() {
					@Override
					protected Integer call() throws Exception {

//						downloadUrl = "http://youku.cdn4-okzy.com/20191126/2980_2373c5f5/1000k/hls/index.m3u8";
//						downloadUrl = "https://video.huishenghuo888888.com/putong/20200109/Nd9lGHjL/500kb/hls/index.m3u8";
//
//						dir = "C:\\Users\\kyh\\Desktop\\m3u8\\sn";

						list = M3U8.getList(downloadUrl);
						int size = list.size();
						ArrayBlockingQueue<String> arrayBlockingQueue = new ArrayBlockingQueue<String>(size);
						arrayBlockingQueue.addAll(list);
						progressBarTextService.restart();

						String prePath = downloadUrl.substring(0, downloadUrl.lastIndexOf("/") + 1);
						// 下载视频片段，分成多个线程下载
						for (int i = 0; i < num; i++) {
							es.execute(new DownloadRunnable(prePath, dir, arrayBlockingQueue, atomicInteger));
						}
						//
						TimerTask timerTask = new TimerTask() {

							@Override
							public void run() {
								int a = atomicInteger.get();
								double p = (double) 100 * a / size;
								System.out.println("timerTask:" + a + "," + p);
								updateProgress(p);
							}
						};
						timer.scheduleAtFixedRate(timerTask, 0, 500);

						return null;
					}

					public void updateProgress(double x) {
						updateProgress(x, 100);
					}

				};
				return task;
			};
		};

		progressBarTextService = new Service<String>() {

			@Override
			protected Task<String> createTask() {
				Task<String> task = new Task<String>() {

					@Override
					protected String call() throws Exception {
						TimerTask timerTask = new TimerTask() {

							@Override
							public void run() {
								int a = atomicInteger.get();
								double p = (double) 100 * a / list.size();
								String f = String.format("%.2f%%", p);
								System.out.println("timerTask2:" + f);
								updateTitle(f);
							}
						};
						timer.scheduleAtFixedRate(timerTask, 0, 500);
						return null;
					}
				};
				return task;
			};
		};

		progressBar.progressProperty().bind(progressBarService.progressProperty());
		progressBarService.restart();
		progressBarTextLabel.textProperty().bind(progressBarTextService.titleProperty());

		
		
		
		megerButton.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (null == dir || "".equals(dir.trim())) {
					Alert urlAlert = new Alert(AlertType.ERROR);
					Stage window = (Stage) urlAlert.getDialogPane().getScene().getWindow();
					Image image = CommonUtility.getImage("title.png");
					window.getIcons().add(image);
					urlAlert.setHeaderText("保存路径不能为空!");
					urlAlert.setTitle("提示");
					urlAlert.show();
				} else {
					FFMPEG.merge(dir);
				}

			}
		});
	}
}
