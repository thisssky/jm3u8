package application.utils;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import application.dto.EXTINF;
import application.dto.EXTM3U;

public class JAXB {

	public static JAXBContext context;
	static {
		Class<EXTM3U> rootClass = EXTM3U.class;

		try {
			// creating the JAXBUtils context
			context = JAXBContext.newInstance(rootClass);
			// creating the marshaller object
			// Marshaller marshaller = context.createMarshaller();
			// 格式化输出，即按标签自动换行，否则就是一行输出
			// marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// 设置编码（默认编码就是utf-8）
			// marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			// 是否省略xml头信息，默认不省略（false）
			// marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static synchronized EXTM3U read(File file) {
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			EXTM3U xmlRoot = (EXTM3U) unmarshaller.unmarshal(file);
			return xmlRoot;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static synchronized void insert(String dir, String fileType, EXTINF extinf) {
		File file = new File(dir + File.separator + fileType);
		EXTM3U root = read(file);
		List<EXTINF> list = root.getList();
		list.add(extinf);
		list.sort(new Comparator<EXTINF>() {

			@Override
			public int compare(application.dto.EXTINF o1, application.dto.EXTINF o2) {
				return o1.getIndex() - o2.getIndex();
			}
		});
		update(file, root);
	}

	@Deprecated
	public static synchronized void delete(String fileType, EXTINF extinf) {
		File file = new File("dir" + File.separator + fileType);
		EXTM3U root = read(file);
		List<EXTINF> list = root.getList();
		EXTINF deleteExtinf = null;
		for (EXTINF extinf2 : list) {
			if (extinf2.getIndex() == extinf.getIndex()) {
				deleteExtinf = extinf2;
				break;
			}
		}
		list.remove(deleteExtinf);
		update(file, root);
	}

	public static synchronized void update(File file, EXTM3U root) {

		try {
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
			marshaller.marshal(root, file);
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	public static void extinf(String m3u8, Boolean encrypted, String dir, List<EXTINF> ts) {
		EXTM3U root = new EXTM3U();
		root.getList().addAll(ts);
		root.setDir(dir);
		root.setEncrypt(encrypted);
		root.setUrl(m3u8);
		root.setTotal(ts.size());
		update(new File(dir + File.separator + Constants.EXTINF_TYPE), root);
	}

	public static boolean extinfExists(String dir) {
		File file = new File(dir + File.separator + Constants.EXTINF_TYPE);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	public static EXTM3U readExtinf(String dir) {
		File file = new File(dir + File.separator + Constants.EXTINF_TYPE);
		EXTM3U xmlRoot = read(file);
		return xmlRoot;
	}

	public static synchronized void error(String dir, EXTINF extinf) {

		File errorFile = new File(dir + File.separator + Constants.ERROR_TYPE);
		if (errorFile.exists()) {
			insert(dir, Constants.ERROR_TYPE, extinf);
		} else {
			EXTM3U root = new EXTM3U();
			root.getList().add(extinf);
			update(errorFile, root);
		}

	}

}
