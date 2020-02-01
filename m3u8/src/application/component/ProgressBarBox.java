package application.component;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import application.dto.EXTINF;
import application.runnable.DownloadRunnable;
import application.utils.JAXBUtils;
import application.utils.M3U8;
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
	private Service<String> progressBarTextService;
	private AtomicInteger atomicInteger = new AtomicInteger(0);
	private List<EXTINF> list;
	private String m3u8;
	private String dir;
	private int num = 4;
	private Timer timer = new Timer();
	private ExecutorService es = Executors.newFixedThreadPool(num);

	public ProgressBarBox(String m3u8, String dir) {
		this.m3u8 = m3u8;
		this.dir = dir;
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

//		GridPane.setHalignment(progressBarTextLabel, HPos.CENTER);
//		GridPane.setFillHeight(progressBarTextLabel, true);
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
		progressBarService = new Service<Integer>() {

			@Override
			protected Task<Integer> createTask() {
				Task<Integer> task = new Task<Integer>() {
					@Override
					protected Integer call() throws Exception {

						list = M3U8.ts(m3u8, dir);
						JAXBUtils.create(dir, list);
						int size = list.size();
						ArrayBlockingQueue<EXTINF> arrayBlockingQueue = new ArrayBlockingQueue<EXTINF>(size);
						arrayBlockingQueue.addAll(list);
						progressBarTextService.restart();

						for (int i = 0; i < num; i++) {
							es.execute(new DownloadRunnable(dir, arrayBlockingQueue, atomicInteger, size));
						}

						TimerTask timerTask = new TimerTask() {

							@Override
							public void run() {
								int a = atomicInteger.get();

								double p = (double) 100 * a / size;
//								System.out.println("timerTask:" + a + "," + p);
								updateProgress(p);
							}
						};
						timer.scheduleAtFixedRate(timerTask, 0, 1000);

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
//								System.out.println("timerTask2:" + f);
								updateTitle(f);
							}
						};
						timer.scheduleAtFixedRate(timerTask, 0, 1000);
						return null;
					}
				};
				return task;
			};
		};

		progressBar.progressProperty().bind(progressBarService.progressProperty());
		progressBarService.restart();
		progressBarTextLabel.textProperty().bind(progressBarTextService.titleProperty());

	}
}
