package com.yusihu.text.dedup.analyzer;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yusihu
 * @date 2024-07-03 19:36
 */
@Slf4j
public class JiebaAnalyzer extends AbstractAnalyzer{


    private static class SingleHolder{
        private static final JiebaAnalyzer INSTANCE = new JiebaAnalyzer();
    }

    public static JiebaAnalyzer getInstance() {
        return SingleHolder.INSTANCE;
    }

    private JiebaAnalyzer() {
        final URL url = JiebaAnalyzer.class.getClassLoader().getResource("dict/jieba.txt");
        if(url == null) {
            log.info("can not find user dict at path: dict/jieba.txt ");
        } else {
            final String fileName = url.getPath();
            Path path = Paths.get(new File(fileName).getAbsolutePath());
            WordDictionary.getInstance().loadUserDict( path );
        }
    }

    @Override
    public List<String> wordSegment(String text) {
        final JiebaSegmenter segmenter = new JiebaSegmenter();
        final List<String> words = segmenter.sentenceProcess(text);
        words.removeAll(stopWords);
        return words;
    }


    public static void main(String[] args) {
        JiebaAnalyzer analyzer = new JiebaAnalyzer();
        String text = "小米14今日发售，预约火爆，开售即抢光！";
        final List<String> list = analyzer.wordSegment(text);
        System.out.println(list);
        final Map<String, Integer> map = analyzer.wordAnalyzer(text);
        System.out.println(map);
    }
}
