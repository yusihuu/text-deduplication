package com.yusihu.text.dedup.analyzer;

import com.yusihu.text.dedup.hash.SimHash;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yusihu
 * @date 2024-07-05 10:18
 */
public abstract class AbstractAnalyzer implements Analyzer{

    protected static List<String> stopWords;

    public AbstractAnalyzer() {
        try {
            stopWords = this.readStopWords();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Integer> wordAnalyzer(String text) {
        return this.wordCount(this.wordSegment(text));
    }

    /**
     * 分词
     * @param text 文本
     * @return list
     */
    @Override
    public abstract List<String> wordSegment(String text);



    /**
     * words 词频统计
     * @param words 关键词
     * @return map
     */
    private Map<String, Integer> wordCount(List<String> words) {
        Map<String, Integer> map = new HashMap<>(words.size());
        for (String word : words) {
            map.put(word, map.getOrDefault(word, 0) + 1);
        }
        return map;
    }

    private List<String> readStopWords() throws IOException {
        List<String> stopWords = new ArrayList<>();
        stopWords.add(" ");
        final InputStream inputStream = AbstractAnalyzer.class.getClassLoader().getResourceAsStream("stopWords.txt");
        if (inputStream == null) {
            System.out.println("stopWords file not exist");
            return stopWords;
        }
        try (
                InputStreamReader reader = new InputStreamReader(inputStream, Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(reader)
        ) {
            for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
                stopWords.add(line);
            }
        }
        return stopWords;
    }
}
