package qiu.utils;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

public class CloseUtil {
    /**
     * 关闭流操作的封装方法
     * */
    public static void close(InputStream is){
        if(is != null){
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                PrintUtil.print("关闭InputStream失败");
            }
        }
    }
    /**
     * 关闭数据库Connection的封装方法
     * */
    public static void close(Connection conn){
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                PrintUtil.print("关闭Connection失败");
            }
        }
    }
    public static void close(PreparedStatement conn){
        if(conn != null){
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                PrintUtil.print("关闭PreparedStatement失败");
            }
        }
    }

}
