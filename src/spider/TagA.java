package spider;

import iktest.SAA;

public class TagA {
	
	private String text = "";
	private String url = "";
	private String title = "";
	private String tag = "";
	private float priority = 0;
	
	public TagA(String a) {
		this.setTag(a);
		this.setText(Spider.regFind("<a[\\s\\S]*?>([\\s\\S]*?)</a>", 1, a));
		this.setTitle(Spider.regFind("title=\"([\\s\\S]*?)\"", 1, a));
		this.setUrl(Spider.regFind("href=\"([\\s\\S]*?)\"", 1, a));
		this.setPriority(SAA.getPriority(this.getUrl(), this.getTitle(), this.getText()));
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public float getPriority() {
		return priority;
	}

	public void setPriority(float priority) {
		this.priority = priority;
	}
	
	
}
