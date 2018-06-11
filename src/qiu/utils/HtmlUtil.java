package qiu.utils;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static spider.Spider.regFind;

public class HtmlUtil {
    /**
     *
     * @param html
     * @return 获得链接
     */
    public static List<String> getLink(String html){
        html = html.replaceAll("<img[\\s\\S]*?/>", "");
        final List<String> list = new ArrayList<String>();
        String regex = "<a[^>]*href=(\"([^\"]*)\"|\'([^\']*)\'|([^\\s>]*))[^>]*>(.*?)</a>";
        final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        final Matcher matcher = pattern.matcher(html);
        while (matcher.find()){
            list.add(matcher.group());
        }
        return list;
    }
    /**
     * 获取指定URL的内容
     * */
    public static String getHtml(String urlStr) {
        URL url = null;
        URLConnection conn;
        InputStream is = null;
        try {
            url = new URL(urlStr);
            conn = url.openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            is = (InputStream) conn.getContent();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int buffer = 1024;
            byte[] b = new byte[buffer];
            int n = 0;
            while ((n = is.read(b, 0, buffer)) > 0) {
                baos.write(b, 0, n);
            }
            String htmlResult = new String(baos.toByteArray(), "UTF-8");
            return htmlResult;

        } catch (MalformedURLException e) {
            e.printStackTrace();
            PrintUtil.print("获取网页失败");
        } catch (IOException e) {
            e.printStackTrace();
            PrintUtil.print("获取网页失败");
        } finally {
            CloseUtil.close(is);
        }
        return "";
    }
    /**
     * 将HTML 字符串进行处理，删去不需要的内容
     * */
    public static String formatHtml(String reg, int group, String text) {
        Pattern p_script;
        Matcher m_script;
        Pattern p_style;
        Matcher m_style;
        Pattern p_html;
        Matcher m_html;
        String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; //定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script> }
        String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; //定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style> }
        String regEx_html = "<[^>]+>"; //定义HTML标签的正则表达式
        String htmlStr = regFind(reg, group, text);
        p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
        m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll(""); //过滤script标签
        p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
        m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll(""); //过滤style标签

        /*p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
        m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll(""); //过滤html标签
        htmlStr = htmlStr.replace("&nbsp;", "");//这是过滤空格的标签，把原来的空格换成空格键*/
        return htmlStr.trim();
    }

}
