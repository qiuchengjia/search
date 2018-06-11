package qiu.spider;

public class SpiderMain {
    private SpiderThreadPool spiderThreadPool;
    public static void main(String [] args){
        SpiderMain spiderMain = new SpiderMain();
        spiderMain.spiderThreadPool = new SpiderThreadPool();

        spiderMain.spiderThreadPool.execute
                (new SpiderRunnable("鹅宝宝" , "http://www.baidu.com"));
        spiderMain.spiderThreadPool.execute
                (new SpiderRunnable("邱宝宝" , "http://sports.sina.com.cn/global/"));
    }
}
