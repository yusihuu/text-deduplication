package com.yusihu.text.dedup.dao;

import com.yusihu.text.dedup.entity.Doc;
import com.yusihu.text.dedup.entity.WordDocs;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yusihu
 * @date 2024-07-07 16:57
 */
public interface WordDocsRepository extends MongoRepository<WordDocs, String> {
}
