package qiu.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Document操作工具类
 */
public class DocumentUtil {
    public static String PatternFind(String patternStr, int group, String content) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()){
            return matcher.group(group);
        }
        return "";
    }
}
