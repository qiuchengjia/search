package view;

import java.awt.Label;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import iktest.SAA;
import iktest.Search;
import spider.Spider;


public class MainFrame{
	
	private String defaultUrl = "http://sports.sina.com.cn/global/";
	private JPanel myPanel = null;
	private Label urlLabel = null;
	private JTextField urlTextField = null;
	private JButton spiderStartButton = null;
	private JButton spiderStopButton = null;
	private Spider spider = null;
	private saaThread saat;
	private Label searchLabel = null;
	private JButton doSearchButton = null;
	private JTextField searchTextField = null;
	private JList searchResultList = null;
	private JScrollPane resultListScroll = null;
	

	public MainFrame(int width, int height, String title) {
		JFrame myFrame = new JFrame(title);
		myFrame.setSize(width,height);
		int screenWidth = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth();
		int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
		myFrame.setLocation((screenWidth-width)/2,(screenHeight-height)/2);
		myFrame.setResizable(false);
		
		myPanel = new JPanel();
		myFrame.add(myPanel);
		myPanel.setLayout(null);
		urlLabel = new Label("web site url:", Label.RIGHT);
		urlLabel.setLocation(width/15, height/20);
		urlLabel.setSize(width/10, height/20);
		urlTextField = new JTextField();
		urlTextField.setText(this.defaultUrl);
		urlTextField.setSize(width/2, height/20);
		urlTextField.setLocation(urlLabel.getX()+urlLabel.getWidth()+10, urlLabel.getY());
		spiderStartButton = new JButton("start");
		spiderStartButton.setFocusPainted(false);
		spiderStartButton.setBounds(urlTextField.getX()+urlTextField.getWidth()+10,
				urlLabel.getY(), width/10, height/20);
		spiderStopButton = new JButton("stop");
		spiderStopButton.setFocusPainted(false);
		spiderStopButton.setBounds(spiderStartButton.getX()+spiderStartButton.getWidth()+10,
				urlLabel.getY(),width/10, height/20);
		spiderStopButton.setEnabled(false);
		searchLabel = new Label("words:", Label.RIGHT);
		searchLabel.setBounds(urlLabel.getX(), urlLabel.getY()+urlLabel.getHeight()+10,
				urlLabel.getWidth(), urlLabel.getHeight());
		searchTextField = new JTextField();
		searchTextField.setBounds(urlTextField.getX(), urlTextField.getY()+urlTextField.getHeight()+10,
				urlTextField.getWidth(), urlTextField.getHeight());
		doSearchButton = new JButton("go!");
		doSearchButton.setFocusPainted(false);
		doSearchButton.setBounds(spiderStartButton.getX(), spiderStartButton.getY()+10+spiderStartButton.getHeight(),
				spiderStartButton.getWidth(), spiderStartButton.getHeight());
		searchResultList = new JList();
//		searchResultList.setBounds(searchTextField.getX(), doSearchButton.getY()+spiderStartButton.getHeight()+10,
//				spiderStartButton.getX()-searchTextField.getX()+spiderStopButton.getWidth(), height-200);		
		resultListScroll = new JScrollPane(searchResultList);
		resultListScroll.setBounds(searchTextField.getX(), doSearchButton.getY()+spiderStartButton.getHeight()+10,
				spiderStartButton.getX()-searchTextField.getX()+spiderStopButton.getWidth(), height-200);
		
		
		myPanel.add(urlLabel);
		myPanel.add(urlTextField);
		myPanel.add(spiderStartButton);
		myPanel.add(spiderStopButton);
		myPanel.add(searchLabel);
		myPanel.add(searchTextField);
		myPanel.add(doSearchButton);
		myPanel.add(resultListScroll);
//		myPanel.add(searchResultList);
		spiderStartButton.addActionListener(new SpiderButtonListener());
		spiderStopButton.addActionListener(new SpiderButtonListener());
		doSearchButton.addActionListener(new DoSearchButtonListener());
		
		
		myPanel.setVisible(true);
		myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myFrame.setVisible(true);
		
		try {
			List<String> lists = new ArrayList<>();
			lists.add(this.defaultUrl);
			spider = new Spider(this.defaultUrl);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		saat = new saaThread();
		
	}
	
	public void setURL(String url) {
		urlTextField.setText(url);
		this.defaultUrl = url;
	}
	
	class saaThread extends Thread {
		@Override
		public void run() {
			try {
				SAA.doSaa(spider);
				urlTextField.setEnabled(true);
				spiderStartButton.setEnabled(true);
				spiderStopButton.setEnabled(false);
				JOptionPane.showMessageDialog(null, "退火爬取完毕");

			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
	}
	
	private class DoSearchButtonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(doSearchButton.getText())) {
				String words = searchTextField.getText();
				if (words.equals("") || words==null) {
					JOptionPane.showMessageDialog(null, "不能为空", "alert", JOptionPane.ERROR_MESSAGE);
				}else{
					Search search = Search.getSearchTool();
					ArrayList<String> resultList = search.Query(words);
					if (resultList.size()>0) {
						String ids = list2String(resultList, ",");
						String sql = "select id, title, url from pages where id in ("+ids+") order by field (id,"+ids+")";
						ResultSet rs = Search.getSearchTool().doSql(sql);
						try {
							if (!rs.next()) {
								JOptionPane.showMessageDialog(null, "未找到相关网页", "alert", JOptionPane.ERROR_MESSAGE);
							}else {
								rs.previous();
								DefaultListModel dlm = new DefaultListModel<>();
								while (rs.next()) {
									int id = rs.getInt("id");
									String title = rs.getString("title");
									String url = rs.getString("url");
									String listObject = title+"("+url+")";
									dlm.addElement(listObject);
								}
								searchResultList.setModel(dlm);
							}
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}else {
						searchResultList.setModel(new DefaultListModel<>());
						JOptionPane.showMessageDialog(null, "未找到相关网页", "alert", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		}
		
	}
	
	String list2String(ArrayList list, String buff) {
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<list.size(); i++) {
			sb.append(list.get(i));
			sb.append(buff);
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}
	
	private class SpiderButtonListener implements ActionListener {

		@SuppressWarnings("deprecation")
		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals(spiderStartButton.getText())) {
				if (urlTextField.getText().equals("")
						|| urlTextField.getText().replaceAll("\\s", "").equals("")) {
					JOptionPane.showMessageDialog(null, "url不能为空", "alert", JOptionPane.ERROR_MESSAGE);
				}else {
					urlTextField.setEnabled(false);
					spiderStartButton.setEnabled(false);
					spiderStopButton.setEnabled(true);
					try {
						spider = new Spider(urlTextField.getText());
						saat.start();
					} catch (Exception e1) {
					}
				}
				
			}else if (e.getActionCommand().equals(spiderStopButton.getText())) {
				urlTextField.setEnabled(true);
				spiderStartButton.setEnabled(true);
				spiderStopButton.setEnabled(false);
				saat.stop();
			}
		}
		
	}
	
}
