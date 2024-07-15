package com.yusihu.text.dedup.hash;

import com.yusihu.text.dedup.analyzer.JiebaAnalyzer;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yusihu
 * @date 2024-07-04 17:49
 */
public class JaccardSimilarity {

    private static class SingleHolder{
        private static final JaccardSimilarity INSTANCE = new JaccardSimilarity();
    }

    public static JaccardSimilarity getInstance() {
        return JaccardSimilarity.SingleHolder.INSTANCE;
    }

    public Double calculate(Set<String> words1, Set<String>  words2) {
        int size1 = words1.size();
        int size2 = words2.size();
        if (size1 == 0 && size2 == 0) {
            return 1.0D;
        } else if (size1 != 0 && size2 != 0) {
            Set<String> unionSet = new HashSet<>(words1);
            unionSet.addAll(words2);
            int intersectionSize = words1.size() + words2.size() - unionSet.size();
            return (double) intersectionSize / (double)unionSet.size();
        } else {
            return 0.0D;
        }
    }

    public Double calculate(CharSequence left, CharSequence right) {
        int leftLength = left.length();
        int rightLength = right.length();
        if (leftLength == 0 && rightLength == 0) {
            return 1.0D;
        } else if (leftLength != 0 && rightLength != 0) {
            Set<Character> leftSet = new HashSet<>();

            for(int i = 0; i < leftLength; ++i) {
                leftSet.add(left.charAt(i));
            }

            Set<Character> rightSet = new HashSet();

            for(int i = 0; i < rightLength; ++i) {
                rightSet.add(right.charAt(i));
            }

            Set<Character> unionSet = new HashSet(leftSet);
            unionSet.addAll(rightSet);
            int intersectionSize = leftSet.size() + rightSet.size() - unionSet.size();
            return (double) intersectionSize / (double)unionSet.size();
        } else {
            return 0.0D;
        }
    }

    public static void main(String[] args) {

    }
}
