package com.yusihu.text.dedup.hash;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.yusihu.text.dedup.analyzer.Analyzer;
import com.yusihu.text.dedup.analyzer.JiebaAnalyzer;
import com.yusihu.text.dedup.constant.SimHashSegment;
import lombok.Getter;

import javax.swing.text.Segment;
import java.io.*;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.*;

/**
 * @author yusihu
 * @date 2024-07-03 10:19
 */
public class SimHash {
    private final int hashBits  =64;
    @Getter
    private BigInteger intSimHash;
    @Getter
    private String strSimHash;

    private BitSet bitSet = new BitSet(hashBits);


    public void simHash(Map<String, Integer> wordsWeights) {
        int[] vector = new int[this.hashBits];
        for (Map.Entry<String, Integer> entry : wordsWeights.entrySet()) {
            final String token = entry.getKey();
            final Integer weight = entry.getValue();
            BigInteger t = this.hash(token);
            for (int i = 0; i < this.hashBits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
                if (t.and(bitmask).signum() != 0) {
                    // 计算整个文档的所有特征的向量和
                    vector[i] += weight;
                } else {
                    vector[i] -= weight;
                }
            }
        }

        BigInteger fingerprint = new BigInteger("0");
        StringBuilder simHashBuffer = new StringBuilder();
        for (int i = 0; i < this.hashBits; i++) {
            // 4、最后对数组进行判断,大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹/签名.
            if (vector[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
                simHashBuffer.append("1");
                bitSet.set(i);
            } else {
                simHashBuffer.append("0");
            }
        }
        this.strSimHash = simHashBuffer.toString();
        this.intSimHash = fingerprint;
    }

    public BigInteger calSimHash(Map<String, Integer> wordsWeights) {
        int[] vector = new int[this.hashBits];
        for (Map.Entry<String, Integer> entry : wordsWeights.entrySet()) {
            final String token = entry.getKey();
            final Integer weight = entry.getValue();
            BigInteger t = this.hash(token);
            for (int i = 0; i < this.hashBits; i++) {
                BigInteger bitmask = new BigInteger("1").shiftLeft(i);
                // 3、建立一个长度为64的整数数组(假设要生成64位的数字指纹,也可以是其它数字),
                // 对每一个分词hash后的数列进行判断,如果是1000...1,那么数组的第一位和末尾一位加1,
                // 中间的62位减一,也就是说,逢1加1,逢0减1.一直到把所有的分词hash数列全部判断完毕.
                if (t.and(bitmask).signum() != 0) {
                    // 计算整个文档的所有特征的向量和
                    vector[i] += weight;
                } else {
                    vector[i] -= weight;
                }
            }
        }

        BigInteger fingerprint = new BigInteger("0");
        for (int i = 0; i < this.hashBits; i++) {
            // 4、最后对数组进行判断,大于0的记为1,小于等于0的记为0,得到一个 64bit 的数字指纹/签名.
            if (vector[i] >= 0) {
                fingerprint = fingerprint.add(new BigInteger("1").shiftLeft(i));
            }
        }
       return fingerprint;
    }


    public List<String> segmentHash() {
        List<String> segments = new ArrayList<>();
        if(strSimHash.length() != hashBits) {
            return segments;
        }
        int segment = 4;
        int segmentSize = hashBits / segment;
        for (int i = 0; i < segment; i++) {
            segments.add(SimHashSegment.values()[i] + "_" +
                    strSimHash.substring(i * segmentSize, (i + 1) * segmentSize));
        }
        return segments;
    }
    public int hammingDistance(SimHash other) {
        BigInteger x = this.intSimHash.xor(other.intSimHash);
        int tot = 0;

        // 统计x中二进制位数为1的个数
        // 我们想想，一个二进制数减去1，那么，从最后那个1（包括那个1）后面的数字全都反了，
        // 对吧，然后，n&(n-1)就相当于把后面的数字清0，
        // 我们看n能做多少次这样的操作就OK了。
        while (x.signum() != 0) {
            tot += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return tot;
    }

    public int getDistance(BigInteger simHash1, BigInteger simHash2) {
        BigInteger x = simHash1.xor(simHash2);
        int distance = 0;

        while (x.signum() != 0) {
            distance += 1;
            x = x.and(x.subtract(new BigInteger("1")));
        }
        return distance;
    }

    public int getDistance(String str1, String str2) {
        int distance;
        if (str1.length() != str2.length()) {
            distance = -1;
        } else {
            distance = 0;
            for (int i = 0; i < str1.length(); i++) {
                if (str1.charAt(i) != str2.charAt(i)) {
                    distance++;
                }
            }
        }
        return distance;
    }

    public List<BigInteger> subByDistance(SimHash simHash, int distance) {
        // 分成几组来检查
        int numEach = this.hashBits / (distance + 1);
        List<BigInteger> characters = new ArrayList<>();

        StringBuilder buffer = new StringBuilder();

        int k = 0;
        for (int i = 0; i < this.intSimHash.bitLength(); i++) {
            // 当且仅当设置了指定的位时，返回 true
            boolean sr = simHash.intSimHash.testBit(i);

            if (sr) {
                buffer.append("1");
            } else {
                buffer.append("0");
            }

            if ((i + 1) % numEach == 0) {
                // 将二进制转为BigInteger
                BigInteger eachValue = new BigInteger(buffer.toString(), 2);
                System.out.println("----" + eachValue);
                buffer.delete(0, buffer.length());
                characters.add(eachValue);
            }
        }

        return characters;
    }

    private BigInteger hash(String source) {
        if (source == null || source.length() == 0) {
            return new BigInteger("0");
        } else {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf(((long) sourceArray[0]) << 7);
            BigInteger m = new BigInteger("1000003");
            BigInteger mask = new BigInteger("2").pow(this.hashBits).subtract(new BigInteger("1"));
            for (char item : sourceArray) {
                BigInteger temp = BigInteger.valueOf((long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor(new BigInteger(String.valueOf(source.length())));
            if (x.equals(new BigInteger("-1"))) {
                x = new BigInteger("-2");
            }
            return x;
        }
    }


    public static void main(String[] args) {
        Analyzer analyzer = JiebaAnalyzer.getInstance();

        String text1 = "小米14今日发售，预约火爆，开售即抢光！";
        final Map<String, Integer> wordWeights1 = analyzer.wordAnalyzer(text1);
        SimHash simHash1 = new SimHash();
        simHash1.simHash(wordWeights1);

        System.out.println(simHash1.strSimHash);
        System.out.println(simHash1.strSimHash.length());
        System.out.println(simHash1.intSimHash);
        System.out.println(simHash1.intSimHash.bitLength());
        System.out.println(simHash1.intSimHash.toString(2));
        System.out.println(simHash1.intSimHash.toString(2));
        System.out.println("==============");

        String text2 = "小米14今日发售，预约火爆，开售即抢光！抓紧抢购，主端沉浸式短视频主模型";
        final Map<String, Integer> wordWeights2 = analyzer.wordAnalyzer(text2);
        SimHash simHash2 = new SimHash();
        simHash2.simHash(wordWeights2);

        System.out.println(simHash2.strSimHash);
        System.out.println(simHash2.strSimHash.length());
        System.out.println(simHash2.intSimHash);
        System.out.println(simHash2.intSimHash.bitLength());
        System.out.println("==============");

        System.out.println(simHash1.hammingDistance(simHash2));

    }
}
