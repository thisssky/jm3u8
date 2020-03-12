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
import application.utils.JAXBUtils;

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
			byte[] bytes = new byte[1024];
			int length = 0;
			while ((length = bufferedInputStream.read(bytes)) != -1) {
				bufferedOutputStream.write(bytes, 0, length);
			}
			int incrementAndGet = atomicInteger.incrementAndGet();
			// 更新progressBar
			task.update(incrementAndGet, count);
		} catch (MalformedURLException e) {
			extinf.setTsName("MalformedURLException" + e.getMessage());
			JAXBUtils.error(extinf.getDir(), extinf);
		} catch (IOException e) {
			extinf.setTsName("IOException" + e.getMessage());
			JAXBUtils.error(extinf.getDir(), extinf);
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
				JAXBUtils.error(extinf.getDir(), extinf);
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
