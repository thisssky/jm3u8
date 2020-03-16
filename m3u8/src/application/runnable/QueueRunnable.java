package application.runnable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import application.component.ProgressBarTask;
import application.dto.EXTINF;
import application.utils.JAXBUtils;

public class QueueRunnable implements Runnable {
	private ConcurrentLinkedQueue<EXTINF> queue;
	private AtomicInteger atomicInteger;
	private int size;
	private ProgressBarTask task;

	public QueueRunnable(ProgressBarTask task, ConcurrentLinkedQueue<EXTINF> arrayBlockingQueue,
			AtomicInteger atomicInteger, int size) {
		this.task = task;
		this.queue = arrayBlockingQueue;
		this.atomicInteger = atomicInteger;
		this.size = size;
	}

	@Override
	public void run() {
		EXTINF extinf = null;
		while (null != (extinf = queue.poll())) {
			download(extinf);
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
			int incrementAndGet = atomicInteger.incrementAndGet();
			task.update(incrementAndGet, size);
			// 每更新成功就删除记录文件中的ts记录
			JAXBUtils.delete(JAXBUtils.EXTINF_TYPE, extinf);
		} catch (MalformedURLException e) {
			extinf.setTsName("MalformedURLException" + e.getMessage());
			JAXBUtils.error(extinf);
		} catch (IOException e) {
			extinf.setTsName("IOException" + e.getMessage());
			JAXBUtils.error(extinf);
			download(extinf);
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
