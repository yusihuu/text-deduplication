package com.yusihu.text.dedup.service.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.yusihu.text.dedup.analyzer.Analyzer;
import com.yusihu.text.dedup.analyzer.JiebaAnalyzer;
import com.yusihu.text.dedup.entity.*;
import com.yusihu.text.dedup.hash.SimHash;
import com.yusihu.text.dedup.service.DocService;
import com.yusihu.text.dedup.service.DocSimHashSegmentService;
import com.yusihu.text.dedup.service.RelatedDocsService;
import com.yusihu.text.dedup.entity.DocSimHashSegment.DocSimHash;
import com.yusihu.text.dedup.entity.RelatedDocs.RelatedDocInfo;
import com.yusihu.text.dedup.service.WordDocsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author yusihu
 * @date 2024-07-09 10:47
 */
@Service
@Slf4j
public class DocHashServiceImpl {
    private Analyzer analyzer;
    private SimHash simHash;

    private final DocService docService;
    private final DocSimHashSegmentService docSimHashSegmentService;
    private final RelatedDocsService relatedDocsService;
    private final WordDocsService wordDocsService;

    private final ThreadPoolExecutor executor;

    @Autowired
    public DocHashServiceImpl(DocService docService,
                              DocSimHashSegmentService docSimHashSegmentService,
                              RelatedDocsService relatedDocsService, WordDocsService wordDocsService) {
        this.docService = docService;
        this.docSimHashSegmentService = docSimHashSegmentService;
        this.relatedDocsService = relatedDocsService;
        this.wordDocsService = wordDocsService;

        this.simHash = new SimHash();
        this.analyzer = JiebaAnalyzer.getInstance();
        this.executor = new ThreadPoolExecutor(
                4,
                6,
                60,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(),
                new ThreadFactoryBuilder().setNameFormat("doc-hash-segment-thread-pool-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());;
    }

    public void save(Doc doc) {
        if(doc == null) {
            return;
        }
        final Doc doc1 = this.docService.findById(doc.getId());
        if (doc1 != null) {
            log.info("doc has exited, id={}", doc.getId());
            return;
        }
        // 内存hash计算存储落表
        this.contentHash(doc);
        // 标题分词存储落表
        this.titleSegment(doc);
        // 文章落表
        this.docService.save(doc);
    }

    public void titleSegment(Doc doc) {
        final Set<String> words = new HashSet<>(this.analyzer.wordSegment(doc.getTitle()));
        for (String word : words) {
            List<WordDocs.WordDocInfo> docInfos = this.wordDocsService.search(word);
            if (docInfos == null) {
                docInfos = new ArrayList<>();
            }
            docInfos.add(new WordDocs.WordDocInfo(doc.getId(), doc.getTitle()));
            this.wordDocsService.save(new WordDocs(word, docInfos));
        }
    }

    public void contentHash(Doc doc) {
        // 暂存simHash分段后各段对应的doc，用于更新
        Map<String, List<DocSimHash>> segmentHashDocMap = new ConcurrentHashMap<>(4);
        // 查找到与doc相似的文章
        final RelatedDocs relatedDocs = this.searchRelated(doc, segmentHashDocMap);
        // 落表存储
        this.saveSegmentHashDoc(segmentHashDocMap);
        this.saveRelatedDocs(relatedDocs);
    }

    public RelatedDocs searchRelated(Doc doc, Map<String, List<DocSimHash>> segmentHashDocMap) {
        // 分词
        final Map<String, Integer> wordMap = this.analyzer.wordAnalyzer(doc.getContent());
        // 计算simHash
        this.simHash.simHash(wordMap);
        doc.setContentHash(simHash.getIntSimHash());
        // simHash 分端
        final List<String> segments = this.simHash.segmentHash();
        // 分段查询相似文章
        return this.doSearchRelatedDocs(doc, segments, segmentHashDocMap);
    }



    private RelatedDocs doSearchRelatedDocs(Doc doc, List<String> segments, Map<String, List<DocSimHash>> segmentHashDocMap) {
        RelatedDocs relatedDocs = new RelatedDocs(doc.getId(), new ArrayList<>());
        if (segments == null || segments.isEmpty()) {
            return relatedDocs;
        }
        List<CompletableFuture<RelatedDocs>> futures = new ArrayList<>();
        for (String segment : segments) {
            futures.add(CompletableFuture.supplyAsync(() -> this.doSearchRelatedDocsByHashSegment(doc, segment, segmentHashDocMap), executor));
        }
        CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        final CompletableFuture<List<RelatedDocs>> allResults = allOf.thenApply(v -> futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList()));
        try {
            final List<RelatedDocs> relatedDocsList = allResults.get();
            for (RelatedDocs docs : relatedDocsList) {
                relatedDocs.merge(docs);
            }
        } catch (Exception e) {
            log.error("get relatedDocs error, e=", e);
        }
        return relatedDocs;
    }


    private RelatedDocs doSearchRelatedDocsByHashSegment(Doc doc, String hashSegment,
                                                         Map<String, List<DocSimHash>> segmentHashDocMap) {
        List<RelatedDocInfo> relatedDocInfos = new ArrayList<>();
        List<DocSimHash> docSimHashes = this.docSimHashSegmentService.searchDocSimHashes(hashSegment);
        if (docSimHashes != null && !docSimHashes.isEmpty()) {
            for (DocSimHash docSimHash : docSimHashes) {
                if (docSimHash != null) {
                    final int distance = this.simHash.getDistance(doc.getContentHash(), docSimHash.getContentHash());
                    if (distance <= 3) {
                        relatedDocInfos.add(new RelatedDocInfo(docSimHash.getId(), (double) distance));
                    }
                }
            }
        } else {
            docSimHashes = new ArrayList<>();
        }
        if (null != segmentHashDocMap) {
            docSimHashes.add(new DocSimHash(doc.getId(), doc.getContentHash()));
            segmentHashDocMap.put(hashSegment, docSimHashes);
        }
        return new RelatedDocs(doc.getId(), relatedDocInfos);
    }


    private void saveSegmentHashDoc(Map<String, List<DocSimHash>> segmentHashDocMap) {
        if (!CollectionUtils.isEmpty(segmentHashDocMap)) {
            for (Map.Entry<String, List<DocSimHash>> entry : segmentHashDocMap.entrySet()) {
                this.docSimHashSegmentService.save(new DocSimHashSegment(entry.getKey(), entry.getValue()));
            }
        }
    }

    private void saveRelatedDocs(RelatedDocs relatedDocs) {
        this.relatedDocsService.save(relatedDocs);
        final List<RelatedDocInfo> docInfos = relatedDocs.getRelatedDocInfos();
        if (docInfos != null && !docInfos.isEmpty()) {
            for (RelatedDocInfo docInfo : docInfos) {
                final String id = docInfo.getId();
                final double distance = docInfo.getDistance();
                final RelatedDocs docs = this.relatedDocsService.searchRelatedDocs(id);
                docs.getRelatedDocInfos().add(new RelatedDocInfo(relatedDocs.getId(), distance));
                this.relatedDocsService.save(docs);
            }
        }
    }



}
