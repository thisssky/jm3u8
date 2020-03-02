package application.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "extinf")
@XmlAccessorType(XmlAccessType.FIELD)
public class EXTINF {
	@XmlElement
	private String m3u8;// http://xxx/xxx/xxx.m3u8
	@XmlElement
	private String dir;
	@XmlElement
	private int index;
	@XmlElement
	private String tsName;// 9660a92c9d8000003.ts
	@XmlElement
	private String ts;// http://xxxx/xxxx/xxx/9660a92c9d8000003.ts
	@XmlElement
	private boolean encrypt;

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

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getTsName() {
		return tsName;
	}

	public void setTsName(String tsName) {
		this.tsName = tsName;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public boolean isEncrypt() {
		return encrypt;
	}

	public void setEncrypt(boolean encrypt) {
		this.encrypt = encrypt;
	}

	public EXTINF() {
	}

	public EXTINF(String m3u8, String dir, int index) {
		this.index = index;
		this.m3u8 = m3u8;
		this.dir = dir;
	}

}
