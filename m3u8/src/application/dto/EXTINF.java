package application.dto;

public class EXTINF {
	private int index;
	private String url;// http://xxxx/xxxx/xxx/9660a92c9d8000003.ts
	private String name;// 9660a92c9d8000003.ts

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

	public EXTINF(int index, String url, String name) {
		this.index = index;
		this.url = url;
		this.name = name;
	}
}
