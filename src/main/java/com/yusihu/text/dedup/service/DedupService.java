package com.yusihu.text.dedup.service;

import com.yusihu.text.dedup.entity.Doc;
import com.yusihu.text.dedup.entity.RelatedDocs;
import com.yusihu.text.dedup.entity.RelatedDocs.RelatedDocInfo;

import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-09 20:53
 */
public interface DedupService {

    RelatedDocs dedup(String id);

    RelatedDocs dedup(Doc doc);

}
