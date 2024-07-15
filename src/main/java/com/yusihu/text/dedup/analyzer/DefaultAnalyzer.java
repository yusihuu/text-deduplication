package com.yusihu.text.dedup.analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author yusihu
 * @date 2024-07-05 10:25
 */
public class DefaultAnalyzer extends AbstractAnalyzer{

    @Override
    public List<String> wordSegment(String text) {
        List<String> tokens = new ArrayList<>();
        final StringTokenizer tokenizer = new StringTokenizer(text);
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }

    public static void main(String[] args) {
        Analyzer analyzer = new DefaultAnalyzer();
        String text = "Any fool can write code that a computer can understand. " +
                "Good programmers write code that humans can understand.";
        final List<String> list = analyzer.wordSegment(text);
        System.out.println(list);
        System.out.println(analyzer.wordAnalyzer(text));
    }
}
