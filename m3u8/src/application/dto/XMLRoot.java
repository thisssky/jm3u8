package application.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({ EXTINF.class })
public class XMLRoot {
	@XmlElementWrapper
	@XmlAnyElement(lax = true)
	private List<EXTINF> list = new ArrayList<EXTINF>();

	public List<EXTINF> getList() {
		return list;
	}

	public void setList(List<EXTINF> list) {
		this.list = list;
	}

}
