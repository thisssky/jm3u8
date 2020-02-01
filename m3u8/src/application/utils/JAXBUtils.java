package application.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import application.dto.EXTINF;
import application.dto.XMLRoot;

public class JAXBUtils {
	public static final String XML = "extinf.xml";

	public static <T> XMLRoot<T> read(String dir, Class<T> rootClass) {
		// getting the xml file to read
		File file = new File(dir + File.separator + XML);
		JAXBContext jContext;
		XMLRoot<T> root = null;
		try {
			// creating the JAXBUtils context
			jContext = JAXBContext.newInstance(rootClass);
			// creating the unmarshall object
			Unmarshaller unmarshallerObj = jContext.createUnmarshaller();
			// calling the unmarshall method
			root = (XMLRoot<T>) unmarshallerObj.unmarshal(file);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return root;
	}

	public static <T> void create(String dir, Class<T> rootClass, Object data) {
		// creating the JAXBUtils context
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(rootClass);
			// creating the marshaller object
			Marshaller marshaller = jaxbContext.createMarshaller();
			// 格式化输出，即按标签自动换行，否则就是一行输出
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			// 设置编码（默认编码就是utf-8）
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			// 是否省略xml头信息，默认不省略（false）
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
//		marshaller.marshal(data, new FileOutputStream(new File(dir + File.separator + XML)));
			marshaller.marshal(data, new File(dir + File.separator + XML));
		} catch (JAXBException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws JAXBException, FileNotFoundException {

		List<EXTINF> ts = M3U8.ts("https://youku.cdn4-okzy.com/20191126/2980_2373c5f5/1000k/hls/index.m3u8",
				"C:\\Users\\kyh\\Desktop\\m3u8\\qyn");
		create("C:\\Users\\kyh\\Desktop\\m3u8\\qyn", ts);

	}

	public static void create(String dir, List<EXTINF> ts) {
		Class<XMLRoot> clsClass = XMLRoot.class;
		XMLRoot<EXTINF> root = new XMLRoot<EXTINF>();
		root.getList().addAll(ts);
		JAXBUtils.create(dir, clsClass, root);

	}
}
