package application.runnable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import application.component.ProgressBarTask;
import application.dto.EXTINF;

public class QueueRunnable implements Runnable {
	private ArrayBlockingQueue<EXTINF> queue;
	private AtomicInteger atomicInteger;
	private int size;
	private ProgressBarTask task;
	private volatile boolean stop = false;

	public QueueRunnable(ProgressBarTask task, ArrayBlockingQueue<EXTINF> arrayBlockingQueue,
			AtomicInteger atomicInteger, int size) {
		this.task = task;
		this.queue = arrayBlockingQueue;
		this.atomicInteger = atomicInteger;
		this.size = size;
	}

	@Override
	public void run() {
		BufferedOutputStream bufferedOutputStream = null;
		BufferedInputStream bufferedInputStream = null;
		EXTINF extinf = null;
		try {
			while (!stop) {
				extinf = queue.take();
//				if (null != extinf) {

				File file = new File(extinf.getDir());
				if (!file.exists()) {
					file.mkdirs();
				}
				URL url = new URL(extinf.getTs());
				// 下载资源
				bufferedInputStream = new BufferedInputStream(url.openStream());
				String fileOutPath = extinf.getDir() + File.separator + extinf.getIndex() + "-" + extinf.getTsName();
				bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(fileOutPath)));
				byte[] bytes = new byte[1024];
				int length = 0;
				while ((length = bufferedInputStream.read(bytes)) != -1) {
					bufferedOutputStream.write(bytes, 0, length);
				}
				int incrementAndGet = atomicInteger.incrementAndGet();
				//
				System.out.println(incrementAndGet);
				task.update(incrementAndGet, size);
				if (incrementAndGet == size) {
					System.out.println("停止线程:" + Thread.currentThread().getName() + "," + incrementAndGet);
					break;
				}
//				}
			}
			System.out.println("break==========");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.out.println("MalformedURLException:" + Thread.currentThread().getName() + ":");
		} catch (IOException e) {
			System.err.println("插入队列中:" + Thread.currentThread().getName() + ":" + extinf.getTs());
			queue.offer(extinf);
		} catch (InterruptedException e) {
			System.out.println("InterruptedException:" + Thread.currentThread().getName() + ":");
			e.printStackTrace();
		} finally {
			System.out.println("finally:" + Thread.currentThread().getName() + ":");
			try {
				if (null != bufferedOutputStream) {
					bufferedOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (null != bufferedInputStream) {

				}
				bufferedInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
