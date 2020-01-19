package application.runnable;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public  
	class DownloadRunnable implements Runnable {
		private ArrayBlockingQueue<String> queue;
		private String prePath;
		private String dir;
		private AtomicInteger atomicInteger;

		public DownloadRunnable(String prePath, String dir, ArrayBlockingQueue<String> arrayBlockingQueue,
				AtomicInteger atomicInteger) {
			this.dir = dir;
			this.prePath = prePath;
			this.queue = arrayBlockingQueue;
			this.atomicInteger = atomicInteger;
		}

		@Override
		public void run() {
			FileOutputStream fileOutputStream = null;
			DataInputStream dataInputStream = null;
			String urlpath = null;
			try {
				while (true) {
					urlpath = queue.poll();
					if (null != urlpath) {

						URL url = new URL(prePath + urlpath);
						// 下载资源
						dataInputStream = new DataInputStream(url.openStream());
						String fileOutPath = dir + File.separator + urlpath;
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
				System.err.println("插入队列中:" + urlpath);
				queue.offer(urlpath);
			} finally {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					System.err.println("close err");
					e.printStackTrace();
				}
				try {
					System.err.println("close err");
					dataInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

	}
