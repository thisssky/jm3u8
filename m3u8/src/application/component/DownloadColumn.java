package application.component;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import application.dto.EXTINF;
import application.dto.XMLRoot;
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

public class DownloadColumn extends AnchorPane {
	private String m3u8;
	private String dir;

	private GridPane container;
	private ProgressBar progressBar;
	private Label label;
	private Service<Integer> service;
	private AtomicInteger progress = new AtomicInteger(0);
	private AtomicInteger max = new AtomicInteger(0);
	private AtomicBoolean flag = new AtomicBoolean(false);
	private ConcurrentLinkedQueue<EXTINF> remain = new ConcurrentLinkedQueue<EXTINF>();

	public DownloadColumn(String m3u8, String dir) {
		this.m3u8 = m3u8;
		this.dir = dir;

		progressBar = new ProgressBar();
		progressBar.setPrefHeight(22);
		progressBar.setPrefWidth(200);
		progressBar.setProgress(0);
		label = new Label("0.00%");
		label.setFont(Font.font(16));
		label.setPrefWidth(200);
		label.setTextFill(Color.BLACK);// web("#0076a3"));
		label.setStyle("-fx-alignment:center;");

		container = new GridPane();
		AnchorPane.setTopAnchor(container, 2D);
		AnchorPane.setLeftAnchor(container, 1D);
		container.add(progressBar, 0, 0);
		container.add(label, 0, 0);
		getChildren().add(container);
	}

	/** 下载 */
	public void download() {

		service = new Service<Integer>() {
			@Override
			protected Task<Integer> createTask() {
				DownloadTask task = new DownloadTask(m3u8, dir, flag, progress, remain, max);
				return task;
			};
		};
		service.progressProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				progressBar.setProgress(newValue.doubleValue());
				// 去更新label值
				label.setText(String.format("%.2f%%", newValue.doubleValue() * 100));
			}
		});
		service.start();
	}

	/** 本地文件下载 */
	public void localDownload(XMLRoot xmlRoot) {
		progressBar.setProgress(-1);
		// flag progress, remain, max
		List<EXTINF> list = xmlRoot.getList();
		File file = new File(dir);
		String[] ts = file.list(new FilenameFilter() {

			@Override
			public boolean accept(File file, String name) {
				if (name.endsWith(".ts")) {
					return true;
				}
				return false;
			}
		});
		if (ts.length == list.size()) {
			progressBar.setProgress(1);
			label.setText("100.00%");
		} else {

			flag.set(true);
			progress.set(ts.length);
			Iterator<EXTINF> iterator = list.iterator();
			while (iterator.hasNext()) {
				EXTINF extinf = iterator.next();
				for (int i = 0, len = ts.length; i < len; i++) {
					if (ts[i].equals(extinf.getIndex() + "-" + extinf.getTsName())) {
						iterator.remove();
						break;
					}

				}

			}
			remain.addAll(list);
			max.set(xmlRoot.getTotal());

			download();
		}
	}

	/** 暂停下载 */
	public void suspend() {
		flag.set(false);

	}

	/** 重新下载 */
	public void resume() {
		flag.set(true);
		download();
	}

}
