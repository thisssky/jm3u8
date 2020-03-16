package application.utils;

import java.io.File;
import java.util.Comparator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import application.dto.EXTINF;
import application.dto.XMLRoot;

public class JAXBUtils {
	public static final String EXTINF_TYPE = "extinf.xml";
	public static final String ERROR_TYPE = "error.xml";

	public static JAXBContext context;
	static {
		Class<XMLRoot> rootClass = XMLRoot.class;

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

	public static XMLRoot read(File file) {
		try {
			Unmarshaller unmarshaller = context.createUnmarshaller();
			XMLRoot xmlRoot = (XMLRoot) unmarshaller.unmarshal(file);
			return xmlRoot;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static void insert(String fileType, EXTINF extinf) {
		File file = new File(extinf.getDir() + File.separator + fileType);
		XMLRoot root = read(file);
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

	public static void delete(String fileType, EXTINF extinf) {
		File file = new File(extinf.getDir() + File.separator + fileType);
		XMLRoot root = read(file);
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

	public static void update(File file, XMLRoot root) {

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

	public static void main(String[] args) {
		EXTINF extinf = new EXTINF("", "C:\\Users\\kyh\\Desktop\\m3u8\\zhentan\\02s\\02", 4);
		insert(EXTINF_TYPE, extinf);
	}

	public static void extinf(String dir, List<EXTINF> ts) {
		XMLRoot root = new XMLRoot();
		root.getList().addAll(ts);
		root.setDir(dir);
		root.setEncrypt(ts.get(0).getEncrypt());
		root.setM3u8(ts.get(0).getM3u8());
		root.setTotal(ts.size());
		update(new File(dir + File.separator + EXTINF_TYPE), root);
	}

	public static void error(EXTINF data) {

		File errorFile = new File(data.getDir() + File.separator + ERROR_TYPE);
		if (errorFile.exists()) {
			insert(ERROR_TYPE, data);
		} else {
			XMLRoot root = new XMLRoot();
			root.getList().add(data);
			update(errorFile, root);
		}

	}

}
