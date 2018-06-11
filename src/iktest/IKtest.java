package iktest;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.wltea.analyzer.cfg.DefaultConfig;
import org.wltea.analyzer.cfg.Configuration;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import com.huaban.analysis.jieba.DictSegment;
import com.huaban.analysis.jieba.Hit;
import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.Pair;
import com.huaban.analysis.jieba.WordDictionary;
import com.huaban.analysis.jieba.JiebaSegmenter.SegMode;
import com.huaban.analysis.jieba.viterbi.FinalSeg;

public class IKtest {
    public static void main(String[] args) throws IOException {
         JiebaSegmenter segmenter = new JiebaSegmenter();
         String[] sentences = new String[] {"阿兰破门恒大2-0华夏 正播国安1-1上港","马云马化腾"};
         for (String sentence : sentences) {
        	 //System.out.println(sentence);
          // System.out.println(segmenter.process(sentence, SegMode.SEARCH).toString());
             System.out.println(segmenter.sentenceProcess(sentence).toString());
            
             //System.out.println(segmenter.process(sentence, SegMode.SEARCH).toString());
         }
    }
}