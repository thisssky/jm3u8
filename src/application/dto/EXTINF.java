package application.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "extinf")
@XmlAccessorType(XmlAccessType.FIELD)
public class EXTINF {
	@XmlElement
	private int index;
	@XmlElement
	private String tsName;// 9660a92c9d8000003.ts
	@XmlElement
	private String ts;// http://xxxx/xxxx/xxx/9660a92c9d8000003.ts
	@XmlElement
	private boolean isEncrypted = false;

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

	public boolean isEncrypted() {
		return isEncrypted;
	}

	public void setEncrypted(boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public EXTINF() {
	}

	public EXTINF(int index) {
		this();
		this.index = index;
	}
}
