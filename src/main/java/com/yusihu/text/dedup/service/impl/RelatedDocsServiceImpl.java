package com.yusihu.text.dedup.service.impl;

import com.yusihu.text.dedup.dao.RelatedDocsRepository;
import com.yusihu.text.dedup.entity.RelatedDocs;
import com.yusihu.text.dedup.service.RelatedDocsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * @author yusihu
 * @date 2024-07-09 14:43
 */
@Service
@Slf4j
public class RelatedDocsServiceImpl implements RelatedDocsService {

    private final RelatedDocsRepository relatedDocsRepository;

    @Autowired
    public RelatedDocsServiceImpl(RelatedDocsRepository relatedDocsRepository) {
        this.relatedDocsRepository = relatedDocsRepository;
    }

    @Override
    public RelatedDocs searchRelatedDocs(String id) {
        try {

            RelatedDocs relatedDocs = this.relatedDocsRepository.findById(id).orElse(null);
            if (relatedDocs == null) {
                relatedDocs = new RelatedDocs(id, new ArrayList<>());
            }
            return relatedDocs;
        } catch (Exception e) {
            log.error("searchRelatedDocs error, id=" + id, e);
            return null;
        }
    }

    @Override
    public void save(RelatedDocs relatedDocs) {
        try {
            this.relatedDocsRepository.save(relatedDocs);
        } catch (Exception e) {
            log.error("saveRelatedDocs error, id=" + relatedDocs.getId() + ", docs=" + relatedDocs.getRelatedDocInfos());
        }
    }
}
