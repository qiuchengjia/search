package iktest;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.huaban.analysis.jieba.JiebaSegmenter;

import spider.Spider;
import spider.TagA;

public class SAA {
    public ArrayList<String> stoplist = new ArrayList<String>();
    private HashMap dictmap = new HashMap();
    private static SAA ass = null;

    public SAA() {
        this.getStopList();
        this.getDictTrie();
    }

    private static SAA createStaticSaa() {
        SAA.ass = new SAA();
        return SAA.ass;
    }

    public static void doSaa(Spider spider) throws Exception {
        int output = 0;
        // init
        ArrayList<TagA> tags = null;
        try {
            tags = spider.getTags();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<TagA> urlList = new ArrayList<TagA>();
        float tInit = 100f;
        float t = tInit;
        float c = 0.5f;
        int s = 10;
        int loop = 0;
        float target = 0;
        int k = 0;
        //
        int tempNo = 0;
        int maxNo = 0;
        int no = 0;
        TagA tempTag = tags.get(no);
        String maxUrl = tags.get(no).getUrl();
        float maxPriority = tags.get(no).getPriority();

        do {
            // 选最大值
            for (int i = 0; i < tags.size(); i++) {
                no = i;
                tempTag = tags.get(no);
                if (maxPriority < tempTag.getPriority()) {
                    maxPriority = tempTag.getPriority();
                    maxUrl = tempTag.getUrl();
                    tempNo = no;
                }
            }
            float randJudge = (float) Math.random();
            int sgn = -1;
            if (randJudge - 0.5 > 0) {
                sgn = 1;
            } else if (randJudge == 0.5) {
                sgn = 0;
            }
            float yi = (float) (t * sgn * (Math.pow((1 + 1 / t), Math.abs(2 * randJudge - 1)) - 1));
            int rand = (int) (1 + (int) Math.abs(no * yi));
            float tempPriority = tags.get(rand).getPriority();
            randJudge = (float) Math.random();
            float y = (float) (1 / Math.exp((1 / tempPriority - 1 / maxPriority) / t));
            if (y > randJudge) {
                if (tags.get(rand).getPriority() > target) {
                    urlList.add(tags.get(rand));
                }
                tags.remove(rand);
            } else {
                if (tags.get(tempNo).getPriority() > target - 1) {
                    urlList.add(tags.get(tempNo));
                }
                tags.remove(tempNo);
            }
            if (loop > s) {
                loop = 0;
                k++;
                t = (float) (tInit * Math.pow(0.8, Math.sqrt(k)));
            }
            loop++;
        } while ((!tags.isEmpty()) && t > 0 && urlList.size() < 31);
        ArrayList articles = Spider.getArticles(urlList);
        Spider.insertIntoDB(articles);
    }

    /**
     * 获取权重
     */
    public static float getPriority(String url, String title, String text) {
        SAA ass;
        if (SAA.ass == null) {
            ass = SAA.createStaticSaa();
        } else {
            ass = SAA.ass;
        }
        return ass.getscore(text);
    }

    /**
     * 获取停用词表
     */
    private void getStopList() {
        String fileName = "src/iktest/stopwords.txt";
        InputStream in;
        try {
            in = new FileInputStream(fileName);
            BufferedReader buff = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = buff.readLine()) != null) {
                this.stoplist.add(line);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 获取分词表
     */
    public void getDictTrie() {
        String fileName = "src/iktest/mydict.txt";
        InputStream in;
        try {
            in = new FileInputStream(fileName);
            BufferedReader buff = new BufferedReader(new InputStreamReader(in, "utf-8"));
            String line;
            while ((line = buff.readLine()) != null) {
                dictmap = updateTrie(line, dictmap, 0);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 更新分词表
     */
    private HashMap updateTrie(String string, HashMap map, int index) {
        if (index == string.length()) return map;
        String cha = string.substring(index, index + 1);
        if (!map.containsKey(cha)) {
            map.put(cha, new HashMap());
        }
        map.put(cha, updateTrie(string, (HashMap) map.get(cha), ++index));
        return map;
    }

    public float getscore(String text) {
        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> list = segmenter.sentenceProcess(text);
        if (list.size() == 0) return 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            if (stoplist.contains(list.get(i))) {
                list.remove(i);
            }
        }
        float score = 0;
        int times = 0;
        for (int i = 0; i < list.size(); i++) {
            String word = list.get(i);
            HashMap temp = dictmap;
            for (int j = 0; j < word.length(); j++) {
                String cha = word.substring(j, j + 1);
                if (temp.containsKey(cha)) {
                    temp = (HashMap) temp.get(cha);
                    if (temp.isEmpty() || j == word.length() - 1) times++;
                }
            }
            score += times / list.size();
        }
        return score;
    }
}
