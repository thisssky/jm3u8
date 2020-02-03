package application.component;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import application.dto.EXTINF;
import application.runnable.ListRunnable;
import application.runnable.QueueRunnable;
import application.utils.JAXBUtils;
import application.utils.M3U8;
import javafx.concurrent.Task;

public class ProgressBarTask extends Task<Integer> {
	private int num = 10;
	private String m3u8;
	private String dir;
	private List<EXTINF> list;
	private int size;
	private ExecutorService executorService = Executors.newFixedThreadPool(10);
	private AtomicInteger atomicInteger = new AtomicInteger(0);

	public ProgressBarTask(String m3u8, String dir) {
		this.m3u8 = m3u8;
		this.dir = dir;
	}

	@Override
	protected Integer call() throws Exception {

		// 获取ts文件,并写入本地文件中
		list = M3U8.ts(m3u8, dir);
		JAXBUtils.create(dir, list);
		size = list.size();

		ArrayBlockingQueue<EXTINF> tsQueue = new ArrayBlockingQueue<EXTINF>(size);
		tsQueue.addAll(list);
		for (int i = 0; i < num; i++) {

//			queueDownload(tsQueue);
			listDownlaod(i);
		}

		return null;
	}

	public void queueDownload(ArrayBlockingQueue<EXTINF> tsQueue) {
		executorService.execute(new QueueRunnable(this, tsQueue, atomicInteger, size));
	}

	public void listDownlaod(int i) {
		int p = size / num;
		int fromIndex = i * p;
		int toIndex = i * p + p;
		if (i == num - 1) {
			toIndex = size;
		}
		executorService.execute(new ListRunnable(this, list.subList(fromIndex, toIndex), atomicInteger, size));
	}

	public void update(int workDone, int max) {
		this.updateProgress(workDone, max);
	}
}
