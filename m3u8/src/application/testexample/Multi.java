package application.testexample;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import application.dto.EXTINF;
import application.utils.M3U8;

public class Multi {

	public static void main(String[] args) {
		String m3u8 = "https://youku.cdn4-okzy.com/20191126/2980_2373c5f5/1000k/hls/index.m3u8";
		String dir = "C:\\Users\\kyh\\Desktop\\m3u8\\qyn\\1";
		List<EXTINF> ts = M3U8.ts(m3u8, dir);
//		queue(ts, dir);
		list(ts, dir);
	}

	public static void queue(List<EXTINF> ts, String dir) {
		LinkedBlockingQueue<EXTINF> linkedBlockingQueue = new LinkedBlockingQueue<EXTINF>(ts);
		ScheduledExecutorService service = Executors.newScheduledThreadPool(10);
		for (int i = 0; i < 100; i++) {

			service.scheduleAtFixedRate(new QueueRunnable(linkedBlockingQueue, dir), 0, 1, TimeUnit.MILLISECONDS);
		}
	}

	static class QueueRunnable implements Runnable {
		private LinkedBlockingQueue<EXTINF> ts;
		private String dir;

		public QueueRunnable(LinkedBlockingQueue<EXTINF> ts, String dir) {
			this.ts = ts;
			this.dir = dir;
		}

		@Override
		public void run() {
			System.out.println(1);
			FileOutputStream fileOutputStream = null;
			DataInputStream dataInputStream = null;
			EXTINF extinf = null;
			try {
				extinf = ts.take();

				File file = new File(dir);
				if (!file.exists()) {
					file.mkdirs();
				}
				URL url = new URL(extinf.getTs());
				// 下载资源
				dataInputStream = new DataInputStream(url.openStream());
				String fileOutPath = dir + File.separator + extinf.getIndex() + "-" + extinf.getTsName();
				fileOutputStream = new FileOutputStream(new File(fileOutPath));
				byte[] bytes = new byte[1024];
				int length = 0;
				while ((length = dataInputStream.read(bytes)) != -1) {
					fileOutputStream.write(bytes, 0, length);
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("插入队列中:" + extinf.getTs());
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				System.out.println("finally:" + Thread.currentThread().getName() + ":");
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

	public static void list(List<EXTINF> ts, String dir) {
		int m = ts.size() / 10;
		System.err.println(ts.size());
		for (int i = 0; i < 10; i++) {
			int fromIndex = i * m;
			int toIndex = i * m + m;
			if (i == 10 - 1) {
				toIndex = ts.size();
			}
			List<EXTINF> subList = ts.subList(fromIndex, toIndex);
			System.out.println(fromIndex + "," + toIndex + "," + subList.size());
			new Thread(new ListRunnable(subList, dir)).start();
		}
	}

	static class ListRunnable implements Runnable {
		private List<EXTINF> ts;
		private String dir;

		public ListRunnable(List<EXTINF> ts, String dir) {
			this.ts = ts;
			this.dir = dir;
		}

		@Override
		public void run() {
			BufferedInputStream bufferedInputStream = null;
			BufferedOutputStream bufferedOutputStream = null;
			EXTINF extinf = null;
			try {
				for (int i = 0, len = ts.size(); i < len; i++) {
					extinf = ts.get(i);
					File file = new File(dir);
					if (!file.exists()) {
						file.mkdirs();
					}
					URL url = new URL(extinf.getTs());
					// 下载资源
					bufferedInputStream = new BufferedInputStream(url.openStream());
					String fileOutPath = dir + File.separator + extinf.getIndex() + "-" + extinf.getTsName();
					bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(fileOutPath)));
					byte[] bytes = new byte[1024 * 1024];
					int length = 0;
					while ((length = bufferedInputStream.read(bytes)) != -1) {
						bufferedOutputStream.write(bytes, 0, length);
						bufferedOutputStream.flush();
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
//				System.err.println("插入队列中:" + urlpath);
			} finally {
				System.out.println("finally:" + Thread.currentThread().getName() + ":");
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					bufferedOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
