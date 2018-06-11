package spider;


import iktest.SAA;
import info.monitorenter.cpdetector.io.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spider {

    private String url = null;
    private URL urlObject = null;
    private URLConnection conn;
    private static int indexCount = 5000;//爬虫抓取的条数

    public Spider(String url) throws MalformedURLException, IOException {
        this.url = url;
        this.urlObject = new URL(this.url);
    }

    /**
     * 获取Tags集合
     * */
    public ArrayList<TagA> getTags() throws IOException {
        this.conn = this.urlObject.openConnection();
        this.conn.setRequestProperty("accept", "*/*");
        this.conn.setRequestProperty("connection", "Keep-Alive");
        this.conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        this.conn.connect();
        BufferedReader br = new BufferedReader(new InputStreamReader(this.conn.getInputStream()));
        String line;
        String html = "";
        while ((line = br.readLine()) != null) {
            html += line;
        }
        html = html.replaceAll("<img[\\s\\S]*?/>", "");
        Pattern pattern = Pattern.compile("<a [\\s\\S]*?href[\\s\\S]*?>[\\s\\S]*?</a>");
        Matcher matcher = pattern.matcher(html);
        ArrayList<String> as = new ArrayList<String>();
        while (matcher.find()) {
            as.add(matcher.group());
        }
        ArrayList<TagA> tags = new ArrayList<TagA>();
        for (int i = 0; i < as.size(); i++) {
            tags.add(new TagA(as.get(i)));
        }
        return tags;
    }
    /**
     * 获取URL的HTML内容
     * */
    public static String getHtml(String url) {
        String contentType = "UTF-8";
        try {
            URL urlObject = new URL(url);
            URLConnection conn = urlObject.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            conn.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = conn.getHeaderFields();
           // System.out.println("Content-Type" + "--->" + map.get("Content-Type"));
            // 遍历所有的响应头字段
            List<String> list=map.get("Content-Type");
            if (list.size()>0){
                String type=list.toString().toUpperCase();
                if (type.contains("UTF-8")){
                    contentType = "UTF-8";
                }else if(type.contains("GB2312")){
                    contentType = "GB2312";
                }else if (type.contains("GBK")){
                    contentType = "GBK";
                }else{
                    getCharset(url);
                }
            }else{
                getCharset(url);
            }
            InputStream is = (InputStream) conn.getContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int buffer = 1024;
            byte[] b = new byte[buffer];
            int n = 0;
            while ((n = is.read(b, 0, buffer)) > 0) {
                baos.write(b, 0, n);
            }
            String htmlResult = new String(baos.toByteArray(), contentType);
            System.out.println("URL = "+url);
            return htmlResult;
        } catch (Exception e) {
            return "";
        }

    }
    public static String getCharset(String strurl){
        // 定义URL对象
        URL url = null;
        try {
            url = new URL(strurl);
            HttpURLConnection urlConnection = (HttpURLConnection) url
                    .openConnection();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 网页编码
        String strencoding = null;
        StringBuffer sb = new StringBuffer();
        String line;
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            in.close();
        } catch (Exception e) { // Report any errors that arise
            System.err.println(e);
            System.err
                    .println("Usage:   java   HttpClient   <URL>   [<filename>]");
        }
        String htmlcode = sb.toString();
        // 解析html源码，取出<meta />区域，并取出charset
        String strbegin = "<meta";
        String strend = ">";
        String strtmp;
        int begin = htmlcode.indexOf(strbegin);
        int end = -1;
        int inttmp;
        while (begin > -1) {
            end = htmlcode.substring(begin).indexOf(strend);
            if (begin > -1 && end > -1) {
                strtmp = htmlcode.substring(begin, begin + end).toLowerCase();
                inttmp = strtmp.indexOf("charset");
                if (inttmp > -1) {
                    strencoding = strtmp.substring(inttmp + 7, end).replace(
                            "=", "").replace("/", "").replace("/", "")
                            .replace("/'", "").replace(" ", "");
                    return strencoding;
                }
            }
            htmlcode = htmlcode.substring(begin);
            begin = htmlcode.indexOf(strbegin);
        }

        /**
         * 分析字节得到网页编码
         */
        strencoding = getFileEncoding(url);

        // 设置默认网页字符编码
        if (strencoding == null) {
            strencoding = "UTF-8";
        }

        return strencoding;
    }
    private static CodepageDetectorProxy detector = CodepageDetectorProxy
            .getInstance();
    public static String getFileEncoding(URL url) {
        java.nio.charset.Charset charset = null;
        try {
            charset = detector.detectCodepage(url);
        } catch (Exception e) {
            System.out.println(e.getClass() + "分析" + "编码失败");
        }
        if (charset != null)
            return charset.name();
        return null;
    }
    /**
     * 获取Articles列表
     * */
    public static ArrayList<Article> getArticles(ArrayList<TagA> tags) {
        ArrayList<Article> articles = new ArrayList<Article>();
        for (int i = 0; i < tags.size(); i++) {
            articles.add(new Article(tags.get(i)));
            //if (i==indexCount) break;//结束抓取
        }
        return articles;
    }
    /**
     * 匹配查找
     * */
    public static String regFind(String reg, int group, String text) {
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) return matcher.group(group);
        return "";
    }
    /**
     * 将文章插入数据库
     */
    public static void insertIntoDB(ArrayList<Article> articles)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver").newInstance();
        String url = "jdbc:mysql://localhost:3306/search?useUnicode=true&characterEncoding=utf-8";
        String user = "root";
        String pswd = "";
        Connection conn = (Connection) DriverManager.getConnection(url, user, pswd);
        PreparedStatement selectId = (PreparedStatement) conn.prepareStatement("select id from pages where url = ?");
        PreparedStatement ps = (PreparedStatement) conn.prepareStatement("insert into pages (url, title, content) values (?, ?, ?) ");
        Article article;
        for (int i = 0; i < articles.size(); i++) {
            article = articles.get(i);
            ps.setString(1, article.getUrl());
            ps.setString(2, article.getTitle());
            ps.setString(3, article.getContent());
            selectId.setString(1, article.getUrl());
            ResultSet set = selectId.executeQuery();
            //更新数据
            if (set.next()) {
                PreparedStatement update = (PreparedStatement) conn.prepareStatement
                        ("update pages set content = ?,title = ? where id = ? ");
                update.setString(1, article.getContent());
                update.setString(2, article.getTitle());
                update.setInt(3, set.getInt("id"));
                update.executeUpdate();
                System.out.println("更新记录 id = " + set.getInt("id"));
            } else {
                try {
                    ps.execute();

                } catch (SQLException e) {
                    //e.printStackTrace();
                    System.out.println("Incorrect string value , 插入失败");
                }

            }
            System.out.println("article的内容" + article.getTitle()+"--"+article.getUrl());
        }


    }

    public static void main(String[] args) {
        try {
            new saaThread(new Spider("http://sports.sina.com.cn/global/")).start();
            //new saaThread(new Spider("http://www.qiuchengjia.cn")).start();
            new saaThread(new Spider("http://www.baidu.com")).start();
            new saaThread(new Spider("https://soccer.hupu.com/")).start();
            new saaThread(new Spider("http://sports.163.com/world/")).start();
            new saaThread(new Spider("http://sports.sohu.com/guoneizuqiu.shtml")).start();
            new saaThread(new Spider("http://sports.qq.com/isocce/")).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 执行抓取的线程
     * */
    static class saaThread extends Thread {
        private Spider spider;

        public saaThread(Spider spider) {
            this.spider = spider;
        }

        @Override
        public void run() {
            try {
                SAA.doSaa(spider);
                System.out.println("抓取完毕");
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

    }
}
