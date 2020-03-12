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
	public static final String EXTINF = "extinf.xml";
	public static final String ERROR = "error.xml";

	public static void extinf(String dir, List<EXTINF> ts) {
		Class<XMLRoot> rootClass = XMLRoot.class;
		XMLRoot root = new XMLRoot();
		root.getList().addAll(ts);

		// creating the JAXBUtils context
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(rootClass);
			// creating the marshaller object
			Marshaller marshaller = jaxbContext.createMarshaller();
			// 格式化输出，即按标签自动换行，否则就是一行输出
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// 设置编码（默认编码就是utf-8）
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			// 是否省略xml头信息，默认不省略（false）
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);

			marshaller.marshal(root, new File(dir + File.separator + EXTINF));
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public static void error(String dir, EXTINF data) {
		// creating the JAXBUtils context
		Class<XMLRoot> rootClass = XMLRoot.class;

		try {
			File errorFile = new File(dir + File.separator + ERROR);
			JAXBContext jaxbContext = JAXBContext.newInstance(rootClass);
			// creating the marshaller object
			Marshaller marshaller = jaxbContext.createMarshaller();
			// 格式化输出，即按标签自动换行，否则就是一行输出
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// 设置编码（默认编码就是utf-8）
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			// 是否省略xml头信息，默认不省略（false）
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
			if (errorFile.exists()) {
				// update
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				XMLRoot xmlRoot = (XMLRoot) unmarshaller.unmarshal(errorFile);
				xmlRoot.getList().add(data);
				xmlRoot.getList().sort(new Comparator<EXTINF>() {

					@Override
					public int compare(application.dto.EXTINF o1, application.dto.EXTINF o2) {
						return o1.getIndex() - o2.getIndex();
					}
				});
				marshaller.marshal(xmlRoot, errorFile);
			} else {
				XMLRoot root = new XMLRoot();
				root.getList().add(data);
				marshaller.marshal(root, errorFile);
			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

}
