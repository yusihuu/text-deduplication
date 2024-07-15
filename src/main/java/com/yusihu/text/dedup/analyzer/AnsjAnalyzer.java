package com.yusihu.text.dedup.analyzer;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.checkerframework.checker.units.qual.A;
import org.nlpcn.commons.lang.tire.domain.Forest;
import org.nlpcn.commons.lang.tire.library.Library;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author yusihu
 * @date 2024-07-03 19:27
 */
public class AnsjAnalyzer extends AbstractAnalyzer{


    @Override
    public List<String> wordSegment(String text) {
        StopRecognition filter = new StopRecognition();

        // 过滤标点
        filter.insertStopNatures("w");
        // 过滤空格
        filter.insertStopNatures("null");
        // 过滤数词
        filter.insertStopNatures("m");
        // 过滤停用词
        filter.insertStopWords(stopWords);

        final Result result = ToAnalysis.parse(text).recognition(filter);
        return result.getTerms().stream().map(Term::getName).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        AnsjAnalyzer analyzer = new AnsjAnalyzer();
        String text = "小米14今日发售，预约火爆，开售即抢光！";
        final List<String> list = analyzer.wordSegment(text);
        System.out.println(list.size());
    }
}
