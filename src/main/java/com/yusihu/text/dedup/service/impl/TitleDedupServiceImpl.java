package com.yusihu.text.dedup.service.impl;

import com.yusihu.text.dedup.analyzer.Analyzer;
import com.yusihu.text.dedup.analyzer.JiebaAnalyzer;
import com.yusihu.text.dedup.dao.DocRepository;
import com.yusihu.text.dedup.dao.WordDocsRepository;
import com.yusihu.text.dedup.entity.Doc;
import com.yusihu.text.dedup.entity.RelatedDocs;
import com.yusihu.text.dedup.entity.WordDocs;
import com.yusihu.text.dedup.hash.JaccardSimilarity;
import com.yusihu.text.dedup.service.DedupService;
import com.yusihu.text.dedup.service.DocService;
import com.yusihu.text.dedup.service.WordDocsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @author yusihu
 * @date 2024-07-05 10:48
 */
@Service
public class TitleDedupServiceImpl implements DedupService {

    private final double JACCARD_THRESHOLD;

    private final DocService docService;
    private final WordDocsService wordDocsService;

    private final Analyzer analyzer;
    private final JaccardSimilarity jaccardSimilarity;

    @Autowired
    public TitleDedupServiceImpl(DocService docService, WordDocsService wordDocsService) {
        this.docService = docService;
        this.wordDocsService = wordDocsService;

        this.analyzer = JiebaAnalyzer.getInstance();
        this.jaccardSimilarity = JaccardSimilarity.getInstance();
        JACCARD_THRESHOLD = 0.45;
    }

    @Override
    public RelatedDocs dedup(String id) {
        List<RelatedDocs.RelatedDocInfo> relatedDocInfos = new ArrayList<>();
        final Doc doc = this.docService.findById(id);
        if (doc != null && doc.getTitle() != null) {
            Set<String> words = new HashSet<>(this.analyzer.wordSegment(doc.getTitle()));

            for (String word : words) {
                final List<WordDocs.WordDocInfo> docInfos = this.wordDocsService.search(word);
                if (CollectionUtils.isEmpty(docInfos)) {
                    continue;
                }
                for (WordDocs.WordDocInfo docInfo : docInfos) {
                    Set<String> words2 = new HashSet<>(this.analyzer.wordSegment(docInfo.getTitle()));
                    final Double distance = jaccardSimilarity.calculate(words, words2);
                    if (distance >= JACCARD_THRESHOLD){
                        relatedDocInfos.add(new RelatedDocs.RelatedDocInfo(docInfo.getId(), distance));
                    }

                }
            }

        }
        return new RelatedDocs(id, relatedDocInfos);
    }

    @Override
    public RelatedDocs dedup(Doc doc) {
        return null;
    }

//    public boolean dedup(Doc doc) {
//        boolean isDedup = false;
//        String title = doc.getTitle();
//        if (null == title || title.isEmpty()) {
//            return false;
//        }
//        Set<String> words = new HashSet<>(analyzer.wordSegment(title));
//        for (String word : words) {
//            final List<Doc> docs = cache.get(word);
//            if (docs == null || docs.isEmpty()) {
//                continue;
//            }
//            for (Doc entity : docs) {
//
//            }
//            BitSet bitSet = new BitSet();
//        }
//        return isDedup;
//    }
//
//    public List<String> dedup(List<Doc> docs) {
//        List<String> result = new ArrayList<>();
//        Set<String> titles = new HashSet<>(docs.size());
//        for (Doc doc : docs) {
//            final String title = doc.getTitle();
//            if(null == title || title.isEmpty()) {
//                continue;
//            }
//
//            if(titles.contains(title)) {
//
//            } else {
//
//                titles.add(title);
//            }
//            final List<String> words = analyzer.wordSegment(title);
//        }
//
//        return result;
//    }
//
//
//    private boolean isSimilarity(Doc doc, Set<String> words) {
//        for (String word : words) {
//            final List<Doc> docs = cache.get(word);
//            if (docs == null || docs.isEmpty()) {
//                continue;
//            }
//            for (Doc entity : docs) {
//            }
//        }
//        return false;
//    }
}
