package com.yusihu.text.dedup.dao;

import com.yusihu.text.dedup.entity.DocSimHashSegment;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author yusihu
 * @date 2024-07-09 10:43
 */
public interface DocSimHashSegmentRepository extends MongoRepository<DocSimHashSegment, String> {
}
