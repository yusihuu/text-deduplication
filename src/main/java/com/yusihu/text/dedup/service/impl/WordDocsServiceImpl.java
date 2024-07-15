package com.yusihu.text.dedup.service.impl;

import com.yusihu.text.dedup.dao.WordDocsRepository;
import com.yusihu.text.dedup.entity.WordDocs;
import com.yusihu.text.dedup.entity.WordDocs.WordDocInfo;
import com.yusihu.text.dedup.service.WordDocsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-10 10:31
 */
@Service
@Slf4j
public class WordDocsServiceImpl implements WordDocsService {

    private final WordDocsRepository repository;

    @Autowired
    public WordDocsServiceImpl(WordDocsRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<WordDocInfo> search(String word) {
        try {
            final WordDocs wordDocs = this.repository.findById(word).orElse(null);
            if (null != wordDocs) {
                return wordDocs.getDocInfos();
            }
        } catch (Exception e) {
            log.error("search docs by word, word=" + word, e);
        }
        return null;
    }

    @Override
    public void save(WordDocs wordDocs) {
        try {
            this.repository.save(wordDocs);
        } catch (Exception e) {
            log.error("save word docs, word=" + wordDocs.getWord() + ", docs=" + wordDocs.getDocInfos(), e);
        }
    }
}
