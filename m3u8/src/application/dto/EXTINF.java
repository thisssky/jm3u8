package application.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "extinf")
@XmlAccessorType(XmlAccessType.FIELD)
public class EXTINF {
	@XmlElement
	private String dir;
	@XmlElement
	private String m3u8;// http://xxx/xxx/xxx.m3u8
	@XmlElement
	private int index;
	@XmlElement
	private String url;// http://xxxx/xxxx/xxx/9660a92c9d8000003.ts
	@XmlElement
	private String name;// 9660a92c9d8000003.ts

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public String getM3u8() {
		return m3u8;
	}

	public void setM3u8(String m3u8) {
		this.m3u8 = m3u8;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public EXTINF() {
	}

	public EXTINF(int index, String url, String name) {
		this.index = index;
		this.url = url;
		this.name = name;
	}
}
