package qiu.spider;

import qiu.bean.Document;
import qiu.db.DBManager;
import qiu.utils.HtmlUtil;

import java.util.ArrayList;
import java.util.List;

public class SpiderRunnable implements Runnable {
    private List<String> mUrlList;
    private List<String> mVisitedList;
    private String mThreadName;
    public SpiderRunnable(String threadName , String url){
        this.mThreadName = threadName;
        mUrlList = new ArrayList<>();
        mVisitedList = new ArrayList<>();
        mUrlList.add(url);
    }
    @Override
    public void run() {
        Document document;
        //将页面超链接加入访问列表中
        for(int i = 0; i < mUrlList.size() ; i++){
            String html = HtmlUtil.getHtml(mUrlList.get(i));
            document = new Document(html);
            System.out.println(mThreadName+" --> "+document.getUrl());
            mVisitedList.add(mUrlList.get(i));
            mUrlList.remove(i);
            DBManager.insertDocumentToPages(document);
           // mUrlList.addAll(HtmlUtil.getLink(mUrlList.get(i)));
        }
    }
}
