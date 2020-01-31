package application.runnable;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import application.dto.EXTINF;

public class DownloadRunnable implements Runnable {
	private ArrayBlockingQueue<EXTINF> queue;
	private String dir;
	private AtomicInteger atomicInteger;

	public DownloadRunnable(String dir, ArrayBlockingQueue<EXTINF> arrayBlockingQueue, AtomicInteger atomicInteger) {
		this.dir = dir;
		this.queue = arrayBlockingQueue;
		this.atomicInteger = atomicInteger;
	}

	@Override
	public void run() {
		FileOutputStream fileOutputStream = null;
		DataInputStream dataInputStream = null;
		EXTINF extinf = null;
		try {
			while (true) {
				extinf = queue.poll();
				if (null != extinf) {

					File file = new File(dir);
					if (!file.exists()) {
						file.mkdirs();
					}
					URL url = new URL(extinf.getUrl());
					// 下载资源
					dataInputStream = new DataInputStream(url.openStream());
					String fileOutPath = dir + File.separator + extinf.getIndex() + "-" + extinf.getName();
					fileOutputStream = new FileOutputStream(new File(fileOutPath));
					byte[] bytes = new byte[1024];
					int length = 0;
					while ((length = dataInputStream.read(bytes)) != -1) {
						fileOutputStream.write(bytes, 0, length);
					}
					int incrementAndGet = atomicInteger.incrementAndGet();
					System.out.println(Thread.currentThread().getName() + ":" + incrementAndGet);
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
//			System.err.println("插入队列中:" + urlpath);
			queue.offer(extinf);
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				dataInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
