package spider;

import qiu.utils.HtmlUtil;

/**
 * 文章实体
 * */
public class Article {
	
	private String url = "";
	private String title = "";
	private String content = "";
	
	public Article(TagA a) {
		this.setUrl(a.getUrl());
		String html = Spider.getHtml(this.getUrl());
		this.setTitle(Spider.regFind("<title>([\\s\\S]*?)</title>", 1, html));
		this.setContent(html);
		String htmlBody = HtmlUtil.formatHtml("<body>([\\s\\S]*?)</body>", 1, html);
		this.setContent(htmlBody);
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "Article{" +
				"url='" + url + '\'' +
				", title='" + title + '\'' +
				", content='" + content + '\'' +
				'}';
	}
}
