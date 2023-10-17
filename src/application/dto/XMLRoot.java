package application.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ EXTINF.class })
public class XMLRoot {
	@XmlElement
	private String m3u8;// http://xxx/xxx/xxx.m3u8
	@XmlElement
	private String dir;
	@XmlElement
	private boolean encrypt;
	@XmlElement
	private int total;

	@XmlElementWrapper
	@XmlAnyElement(lax = true)
	private List<EXTINF> list = new ArrayList<>();

	public String getM3u8() {
		return m3u8;
	}

	public void setM3u8(String m3u8) {
		this.m3u8 = m3u8;
	}

	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<EXTINF> getList() {
		return list;
	}

	public void setList(List<EXTINF> list) {
		this.list = list;
	}

}
