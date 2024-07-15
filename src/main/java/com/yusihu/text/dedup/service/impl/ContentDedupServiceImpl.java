package com.yusihu.text.dedup.service.impl;

import com.yusihu.text.dedup.analyzer.Analyzer;
import com.yusihu.text.dedup.analyzer.JiebaAnalyzer;
import com.yusihu.text.dedup.entity.Doc;
import com.yusihu.text.dedup.entity.RelatedDocs.RelatedDocInfo;
import com.yusihu.text.dedup.entity.RelatedDocs;
import com.yusihu.text.dedup.hash.SimHash;
import com.yusihu.text.dedup.service.DedupService;
import com.yusihu.text.dedup.service.RelatedDocsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author yusihu
 * @date 2024-07-05 14:55
 */
@Service
public class ContentDedupServiceImpl implements DedupService {
    private final RelatedDocsService relatedDocsService;

    private final DocHashServiceImpl docHashService;

    @Autowired
    public ContentDedupServiceImpl(RelatedDocsService relatedDocsService,
                                   DocHashServiceImpl docHashService) {
        this.relatedDocsService = relatedDocsService;
        this.docHashService = docHashService;
    }


    @Override
    public RelatedDocs dedup(String id) {
        return this.relatedDocsService.searchRelatedDocs(id);
    }

    @Override
    public RelatedDocs dedup(Doc doc) {
        return this.docHashService.searchRelated(doc, null);
    }
}
