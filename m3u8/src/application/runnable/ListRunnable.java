package application.runnable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import application.component.ProgressBarTask;
import application.dto.EXTINF;

public class ListRunnable implements Runnable {
	private ProgressBarTask task;
	private List<EXTINF> ts;
	private AtomicInteger atomicInteger;
	private int count;

	public ListRunnable(ProgressBarTask task, List<EXTINF> ts, AtomicInteger atomicInteger, int count) {
		this.task = task;
		this.ts = ts;
		this.atomicInteger = atomicInteger;
		this.count = count;
	}

	public void download(EXTINF extinf) {

		BufferedInputStream bufferedInputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			URL url = new URL(extinf.getTs());
			// 下载资源
			bufferedInputStream = new BufferedInputStream(url.openStream());
			String fileOutPath = extinf.getDir() + File.separator + extinf.getIndex() + "-" + extinf.getTsName();
			bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File(fileOutPath)));
			byte[] bytes = new byte[1024 * 1024];
			int length = 0;
			while ((length = bufferedInputStream.read(bytes)) != -1) {
				bufferedOutputStream.write(bytes, 0, length);
				bufferedOutputStream.flush();
			}

			int incrementAndGet = atomicInteger.incrementAndGet();
			// 更新progressBar
			task.update(incrementAndGet, count);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
//			System.err.println("重新下载:" + extinf.getTs());
			download(extinf);
		} finally {
//			System.out.println("finally:" + Thread.currentThread().getName() + ":");
			try {
				if (null != bufferedInputStream) {
					bufferedInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if (null != bufferedOutputStream) {
					bufferedOutputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void run() {
		EXTINF extinf = null;
		for (int i = 0, len = ts.size(); i < len; i++) {
			extinf = ts.get(i);
			download(extinf);
		}

	}

}
