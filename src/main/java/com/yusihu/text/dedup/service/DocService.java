package com.yusihu.text.dedup.service;

import com.yusihu.text.dedup.entity.Doc;

import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-07 17:04
 */
public interface DocService {

    void save(Doc doc);

    Doc findById(String id);

    List<Doc> findAll();
}
