package com.yusihu.text.dedup.service.impl;

import com.yusihu.text.dedup.dao.DocRepository;
import com.yusihu.text.dedup.entity.Doc;
import com.yusihu.text.dedup.service.DocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-07 17:04
 */
@Service
public class DocServiceImpl implements DocService {

    private final DocRepository docRepository;

    @Autowired
    public DocServiceImpl(DocRepository docRepository) {
        this.docRepository = docRepository;
    }


    @Override
    public void save(Doc doc) {
        docRepository.save(doc);
    }

    @Override
    public Doc findById(String id) {
        return this.docRepository.findById(id).orElse(null);
    }

    @Override
    public List<Doc> findAll() {
        return this.docRepository.findAll();
    }
}
