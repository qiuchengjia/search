package qiu.db;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;
import qiu.bean.Document;
import qiu.utils.CloseUtil;
import qiu.utils.PrintUtil;
import spider.Article;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 单例模式
 * 数据库管理类，用来增删改查，关闭连接
 * */
public class DBManager {
    private volatile static DBManager dbManager;
    private static String dbUrl = "jdbc:mysql://localhost:3306/search?useUnicode=true&characterEncoding=utf-8";
    private static String user = "root";
    private static String password = "";
    private static String dbDriver = "com.mysql.jdbc.Driver";
    private DBManager(){

    }
    /**
     * 单例获取对象
     * */
    public static DBManager getSingleton() {
        if (dbManager == null) {
            synchronized (DBManager.class) {
                if (dbManager == null) {
                    dbManager = new DBManager();
                }
            }
        }
        return dbManager;
    }
    /**
     * 将文章插入数据库
     * */
    public static void insertDocumentToPages(Document document){
        Connection conn = null;
        PreparedStatement selectId = null;
        PreparedStatement ps = null;
        PreparedStatement update = null;
        try {
            Class.forName(dbDriver).newInstance();
            conn = (Connection) DriverManager.getConnection(dbUrl , user , password);
            selectId = (PreparedStatement) conn.prepareStatement("select id from pages where url = ?");
            ps = (PreparedStatement) conn.prepareStatement("insert into pages (url, title, content) values (?, ?, ?) ");
            update = (PreparedStatement) conn.prepareStatement("update pages set content = ?,title = ? where id = ? ");
            //Document document ;
         //   for(int i = 0 ;i < documents.size() ; i++){
           //     document = documents.get(i);
                ps.setString(1 , document.getUrl());
                ps.setString(2 , document.getTitle());
                ps.setString(3 , document.getContent());
                selectId.setString(1 , document.getUrl());
                ResultSet set = selectId.executeQuery();
                //更新数据
                if(set.next()){

                    try{
                        update.setString(1 , document.getContent());
                        update.setString(2 , document.getTitle());
                        update.setInt(3 , set.getInt("id"));
                        update.executeUpdate();
                        PrintUtil.print("更新记录 id = " + set.getInt("id"));
                    }catch (SQLException e){
                        e.printStackTrace();
                        PrintUtil.print("id = "+set.getInt("id")+" title = "+document.getTitle()+" url = "+document.getUrl()+" ----> 更新失败");
                    }

                }else{
                    try{
                        ps.execute();
                    }catch (SQLException e){
                        e.printStackTrace();
                        PrintUtil.print("title = "+document.getTitle()+" url = "+document.getUrl()+" ----> 插入失败");
                    }
                }
        //    }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            CloseUtil.close(conn);
            CloseUtil.close(ps);
            CloseUtil.close(selectId);
            CloseUtil.close(update);
        }
    }
}
