package qiu.bean;

import iktest.SAA;
import qiu.utils.DocumentUtil;
import qiu.utils.HtmlUtil;
import spider.Spider;

public class Document {
    private String url = "";
    private String title = "";
    private String content = "";
    public Document(String html){
        html = HtmlUtil.formatHtml("" , 1,html);
        this.setUrl(DocumentUtil.PatternFind("href=\"([\\s\\S]*?)\"",1,html));
        this.setTitle(DocumentUtil.PatternFind("<title>([\\s\\S]*?)</title>",1,html));
        this.setContent(DocumentUtil.PatternFind("<body>([\\s\\S]*?)</body>",1,html));
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
        return "Document{" +
                "url='" + url + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
