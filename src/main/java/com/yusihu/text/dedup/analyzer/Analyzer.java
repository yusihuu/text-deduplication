package com.yusihu.text.dedup.analyzer;

import java.util.List;
import java.util.Map;

/**
 * @author yusihu
 * @date 2024-07-05 10:17
 */
public interface Analyzer {
    /**
     * 分词并计算词频
     * @param text 文本
     * @return map
     */
    Map<String, Integer> wordAnalyzer(String text);

    /**
     * 分词
     * @param text 文本
     * @return list
     */
    List<String> wordSegment(String text);

}
