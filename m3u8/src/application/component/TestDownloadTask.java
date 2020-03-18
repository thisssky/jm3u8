package application.component;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import application.dto.EXTINF;
import application.utils.JAXBUtils;
import application.utils.M3U8;
import javafx.concurrent.Task;

@Deprecated
public class TestDownloadTask extends Task<Integer> {
	private int num;
	private ThreadPoolExecutor executorService;
	private String m3u8;
	private String dir;
	private List<EXTINF> list;
	private int size;
	private AtomicInteger atomicInteger = new AtomicInteger(0);

	public TestDownloadTask(String m3u8, String dir) {
		num = Runtime.getRuntime().availableProcessors();
		executorService = new ThreadPoolExecutor(num, num, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		executorService.allowCoreThreadTimeOut(true);
		this.m3u8 = m3u8;
		this.dir = dir;
	}

//	@Override
//	protected void updateProgress(double workDone, double max) {
//		// TODO Auto-generated method stub
//		super.updateProgress(workDone, max);
//	}

	@Override
	protected Integer call() throws Exception {
		list = M3U8.ts(m3u8, dir);
		JAXBUtils.extinf(dir, list);
		size = list.size();

		executorService.execute(new Runnable() {

			@Override
			public void run() {
				runnn(list.subList(0, list.size() / 2));
			}
		});
		executorService.execute(new Runnable() {

			@Override
			public void run() {
				runnn(list.subList(list.size() / 2, list.size()));
			}
		});

		return null;
	}

	public void runnn(List<EXTINF> ts) {

		EXTINF extinf = null;
		for (int i = 0, len = ts.size(); i < len; i++) {
			extinf = ts.get(i);
			download(extinf);
		}
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
			updateProgress(incrementAndGet, size);
			// 每更新成功就删除记录文件中的ts记录
//			JAXBUtils.delete(JAXBUtils.EXTINF_TYPE, extinf);
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
