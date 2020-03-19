package application.remain;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import application.component.DownloadTask;
import application.dto.EXTINF;
import application.utils.JAXBUtils;

public class TestQueueRunnable implements Runnable {
	private DownloadTask task;
	private AtomicBoolean flag;
	private AtomicInteger progress;
	private ConcurrentLinkedQueue<EXTINF> queue;
	private int size;

	public TestQueueRunnable(DownloadTask task, AtomicBoolean flag, AtomicInteger progress,
			ConcurrentLinkedQueue<EXTINF> queue, int size) {
		this.task = task;
		this.flag = flag;
		this.progress = progress;
		this.queue = queue;
		this.size = size;
	}

	@Override
	public void run() {
		EXTINF extinf = null;
		while (flag.get() && !queue.isEmpty()) {
			extinf = queue.poll();
			if (null != extinf) {
				download(extinf);
			}
		}

	}

	private void download(EXTINF extinf) {
		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		String fileOutPath = extinf.getDir() + File.separator + extinf.getIndex() + "-" + extinf.getTsName();
		try {
			URL url = new URL(extinf.getTs());
			bufferedInputStream = new BufferedInputStream(url.openStream());
			bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(fileOutPath)));
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = bufferedInputStream.read(bytes)) != -1) {
				bufferedOutputStream.write(bytes, 0, length);
			}
			int incrementAndGet = progress.incrementAndGet();
			task.updateProgress(incrementAndGet, size);
		} catch (MalformedURLException e) {
			queue.offer(extinf);
			extinf.setTsName("MalformedURLException" + e.getMessage());
			JAXBUtils.error(extinf);
		} catch (IOException e) {
			extinf.setTsName("IOException" + e.getMessage());
			JAXBUtils.error(extinf);
			queue.offer(extinf);
		} finally {
			try {
				if (null != bufferedInputStream) {
					bufferedInputStream.close();
				}
				if (null != bufferedOutputStream) {
					bufferedOutputStream.close();
				}
			} catch (IOException e) {
				extinf.setTsName("close.IOException" + e.getMessage());
				JAXBUtils.error(extinf);
			}
		}
	}

}
