package com.yusihu.text.dedup.service;

import com.yusihu.text.dedup.entity.WordDocs;
import com.yusihu.text.dedup.entity.WordDocs.WordDocInfo;

import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-10 10:31
 */
public interface WordDocsService {

    List<WordDocInfo> search(String word);

    void save(WordDocs wordDocs);
}
