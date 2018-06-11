package iktest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class Search {
	private Connection connect=null;
	public ArrayList<String> stoplist = new ArrayList<String>();
	private HashMap<String, ArrayList> querymap = new HashMap<String, ArrayList>();
	private HashMap<Integer, List> allmap = new HashMap<Integer, List>();
	private HashMap mainmap = new HashMap();
	public static Search search = null;
	/**
	 * 构造函数，进行一些数据库初始化、获取分词表、获取索引工作
	 * */
	public Search(){
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
		String url="jdbc:mysql://127.0.0.1:3306/search";
		String user = "root";
		String passwd ="";
		
		try {
			connect=(Connection) DriverManager.getConnection(url,user,passwd);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.getStopList();
		this.getTries();
	}
	/**
	 * 获取搜索工具类
	 * */
	public static Search getSearchTool() {
		if (Search.search==null) {
			Search.search = new Search();
		}
		return Search.search;
	}
	/**
	 * 查询操作获取结果List
	 * */
	public ArrayList<String> Query(String query){
		JiebaSegmenter segmenter = new JiebaSegmenter();
		List <String>list = segmenter.sentenceProcess(query);
		for(int i=list.size()-1;i>=0;i--){
			if(stoplist.contains(list.get(i))){
				 list.remove(i);
				}
		}
		ArrayList allid = new ArrayList();
		for(int i=0;i<list.size();i++){
			if(querymap.containsKey(list.get(i))){
				allid.removeAll(querymap.get(list.get(i)));
				allid.addAll(querymap.get(list.get(i)));
			}
		}
		System.out.println(allid);
		System.out.println(list);
		ArrayList ids = getIds(allid,list);
		return ids;//得到要的结果序列
	}
	/**
	 * 获取结果序列ID列表
	 * */
	private ArrayList<String> getIds(ArrayList allid, List<String>list){
		HashMap idScore = new HashMap();
		for(int i=0;i<allid.size();i++){
			float score=0;
			for(int j=0;j<list.size();j++){
				int times=0;
				for(int k=0;k<allmap.get(allid.get(i)).size();k++){
					if(allmap.get(allid.get(i)).get(k).equals(list.get(j))) times++;
				}
				score += ((querymap.get(list.get(j)).size() * times )/ allmap.get(allid.get(i)).size()) / allmap.size(); 
				idScore.put(allid.get(i), score);
			}
		}
		Object[][] idScores=new Object[2][allid.size()];
		Iterator iter = idScore.entrySet().iterator();
		int key=0;
		while(iter.hasNext()){
			Map.Entry entry = (Map.Entry) iter.next();
			idScores[0][key] = entry.getKey();
			idScores[1][key] = entry.getValue(); 
			key++;
		}
		int minIndex=0;
		float temp=0;
		int tempid=0;
		for(int i=0;i<idScores[0].length;i++){
	        minIndex=i;
	        for(int j=i+1;j<idScores[0].length;j++){
	            if((float)idScores[1][j]<(float)idScores[1][minIndex]){
	                minIndex=j;
	            }
	        }
	        if(minIndex!=i){
	            temp=(float)idScores[1][i];
	            tempid = (int)idScores[0][i];
	            idScores[1][i] =idScores[1][minIndex];
	            idScores[0][i] =idScores[0][minIndex];
	            idScores[1][minIndex] = temp;
	            idScores[0][minIndex] = tempid;
	        }
	    }
	    ArrayList ids = new ArrayList();
	    for(int i=0;i<idScores[0].length;i++){
	    	ids.add(idScores[0][i].toString());
	    }
	    return ids;
	}
	
	/**
	 * 进行查询，获取查询的HashMap
	 * */
	private HashMap<String, ArrayList> getQuery(int id, List<String>list, HashMap<String, ArrayList> querymap){
		for(int i=0;i<list.size();i++){
			String words = list.get(i);
			if(querymap.containsKey(words)){
				if(querymap.get(words).contains(id)) continue;
				querymap.get(words).add(id);
			}else{
				ArrayList ids = new ArrayList();
				ids.add(id);
				querymap.put(words, ids);
			}
		}
		return querymap;
	}
	/**
	 * 查询数据库建立索引，并剔除停用词中的词组
	 * */
	public HashMap getTries( ){
		PreparedStatement ps;
		try {
			ps = (PreparedStatement) connect.prepareStatement("select id , title from pages");
			ps.execute();
			ResultSet res = ps.getResultSet();
			while(res.next()){
				int id = res.getInt("id");
				String  title = res.getString("title");
				JiebaSegmenter segmenter = new JiebaSegmenter();
				List <String>list = segmenter.sentenceProcess(title);
				for(int i=list.size()-1;i>=0;i--){
					if(stoplist.contains(list.get(i))){
						 list.remove(i);
						}
				}
				allmap.put(id, list);
				querymap = this.getQuery(id, list, querymap);
			}
			return mainmap;
		} catch (SQLException e) {
			return mainmap;
		}
	}
	/**
	 * 执行SQL操作
	 * */
	public ResultSet doSql(String sql) {
		try {
			PreparedStatement ps = (PreparedStatement) connect.prepareStatement(sql);
			ps.execute();
			ResultSet result = ps.getResultSet();
			return result;
		} catch (SQLException e) {
			return null;
		}
		
	}

	/**
	 * 获取停用词列表
	 * */
	private void getStopList(){
		String fileName = "src/iktest/stopwords.txt";
		InputStream in;
		try {
			in = new FileInputStream(fileName);
			BufferedReader buff =  new BufferedReader(new InputStreamReader ( in));
			String line;
			while((line=buff.readLine())!=null)
			{
				this.stoplist.add(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}		

}
//
