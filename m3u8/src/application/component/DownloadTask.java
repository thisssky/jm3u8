package application.component;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import application.dto.EXTINF;
import application.runnable.QueueRunnable;
import application.runnable.ThreadPool;
import application.utils.JAXBUtils;
import application.utils.M3U8;
import javafx.concurrent.Task;

public class DownloadTask extends Task<Integer> {
	private String m3u8;
	private String dir;
	private AtomicBoolean flag;
	private AtomicInteger progress;
	private ConcurrentLinkedQueue<EXTINF> remain;
	private AtomicInteger max;
	private int thread = 4;

	public DownloadTask(String m3u8, String dir, AtomicBoolean flag, AtomicInteger progress,
			ConcurrentLinkedQueue<EXTINF> remain, AtomicInteger max) {
		this.m3u8 = m3u8;
		this.dir = dir;
		this.flag = flag;
		this.progress = progress;
		this.remain = remain;
		this.max = max;

	}

	@Override
	protected Integer call() throws Exception {
		if (!flag.get()) {
			List<EXTINF> list = M3U8.ts(m3u8, dir);
			remain.addAll(list);
			max.set(list.size());
			JAXBUtils.extinf(dir, list);
			flag.set(true);
		}

		for (int i = 0; i < thread; i++) {
			ThreadPool.execute(new QueueRunnable(this, flag, progress, remain, max.get()));
		}
		return null;
	}

	public void updateProgress(double workDone, double max) {
		super.updateProgress(workDone, max);
	}

}
