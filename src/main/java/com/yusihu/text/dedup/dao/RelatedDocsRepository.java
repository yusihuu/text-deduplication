package com.yusihu.text.dedup.dao;

import com.yusihu.text.dedup.entity.RelatedDocs;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yusihu
 * @date 2024-07-09 10:44
 */
public interface RelatedDocsRepository extends MongoRepository<RelatedDocs, String> {
}
