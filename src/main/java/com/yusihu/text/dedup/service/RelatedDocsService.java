package com.yusihu.text.dedup.service;

import com.yusihu.text.dedup.entity.RelatedDocs;

/**
 * @author yusihu
 * @date 2024-07-09 14:42
 */
public interface RelatedDocsService {

    RelatedDocs searchRelatedDocs(String id);

    void save(RelatedDocs relatedDocs);
}
