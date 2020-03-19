package application.remain;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import application.dto.EXTINF;
import application.runnable.ListRunnable;
import application.runnable.QueueRunnable;
import application.utils.JAXBUtils;
import application.utils.M3U8;
import javafx.concurrent.Task;

public class ProgressBarTask extends Task<Integer> {
	private int num;
	private String m3u8;
	private String dir;
	private List<EXTINF> list;
	private int size;
	private ThreadPoolExecutor executorService;
	private AtomicInteger atomicInteger = new AtomicInteger(0);

	public ProgressBarTask(String m3u8, String dir) {
		num = Runtime.getRuntime().availableProcessors();
		executorService = new ThreadPoolExecutor(num, num, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		executorService.allowCoreThreadTimeOut(true);
		this.m3u8 = m3u8;
		this.dir = dir;
	}

	@Override
	protected Integer call() throws Exception {

		// 获取ts文件,并写入本地文件中
//		https://sina.com-h-sina.com/20180810/7482_e207140b/1000k/hls/index.m3u8
		list = M3U8.ts(m3u8, dir);
		JAXBUtils.extinf(dir, list);
		size = list.size();

		for (int i = 0; i < num; i++) {
			listDownlaod(i);
//			queueDownload(queue);
		}

		return null;
	}

	public void queueDownload(ConcurrentLinkedQueue<EXTINF> queue) {
//		executorService.execute(new QueueRunnable(this, queue, atomicInteger, size));
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
