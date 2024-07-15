package com.yusihu.text.dedup.controller;

import com.yusihu.text.dedup.entity.RelatedDocs.RelatedDocInfo;
import com.yusihu.text.dedup.service.DedupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-09 20:58
 */
@RestController
@RequestMapping("/dedup")
public class TitleDedupController {

    private final DedupService dedupService;

    @Autowired
    public TitleDedupController(@Qualifier("titleDedupServiceImpl") DedupService dedupService) {
        this.dedupService = dedupService;
    }


    @GetMapping("/title/id/{id}")
    public List<RelatedDocInfo> titleDedup(@PathVariable String id) {
        return this.dedupService.dedup(id);
    }
}
