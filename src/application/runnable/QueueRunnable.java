package application.runnable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

import application.component.task.DownloadTask;
import application.dto.EXTINF;
import application.utils.CommonUtility;
import application.utils.JAXBUtils;

public class QueueRunnable implements Runnable {
	private DownloadTask task;
	private String dir;
	private AtomicBoolean flag;
	private AtomicInteger progress;
	private ConcurrentLinkedQueue<EXTINF> queue;
	private int size;
//	private SSLContext sc;
//	SSLSocketFactory socketFactory;

	public QueueRunnable(DownloadTask task,String dir, AtomicBoolean flag, AtomicInteger progress,
			ConcurrentLinkedQueue<EXTINF> queue, int size) {
		this.task = task;
		this.dir=dir;
		this.flag = flag;
		this.progress = progress;
		this.queue = queue;
		this.size = size;

//		try {
//			SSLContext sc = SSLContext.getInstance("SSL", "SunJSSE");
//			X509TrustManager x509TrustManager = new X509TrustManager() {
//
//				@Override
//				public X509Certificate[] getAcceptedIssuers() {
//					return new X509Certificate[] {};
//				}
//
//				@Override
//				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//
//				}
//
//				@Override
//				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//
//				}
//			};
//			sc.init(null, new TrustManager[] { x509TrustManager }, new java.security.SecureRandom());
//			SSLSocketFactory socketFactory = sc.getSocketFactory();
//
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (NoSuchProviderException e) {
//			e.printStackTrace();
//		} catch (KeyManagementException e) {
//			e.printStackTrace();
//		}
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
		String fileOutPath = dir + File.separator + extinf.getIndex() + "-" + extinf.getTsName();
		try {
			URL url = new URL(extinf.getTs());
//			bufferedInputStream = new BufferedInputStream(url.openStream());
			URLConnection openConnection = url.openConnection();
			if (openConnection instanceof HttpsURLConnection) {
				((HttpsURLConnection) openConnection).setSSLSocketFactory(CommonUtility.getSSLSocketFactory());

			}
			bufferedInputStream = new BufferedInputStream(openConnection.getInputStream());
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
			JAXBUtils.error(dir,extinf);
		} catch (IOException e) {
			extinf.setTsName("IOException" + e.getMessage());
			JAXBUtils.error(dir,extinf);
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
				JAXBUtils.error(dir,extinf);
			}
		}
	}

}
