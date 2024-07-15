package com.yusihu.text.dedup.controller;

import com.yusihu.text.dedup.entity.Doc;
import com.yusihu.text.dedup.entity.RelatedDocs;
import com.yusihu.text.dedup.entity.RelatedDocs.RelatedDocInfo;
import com.yusihu.text.dedup.service.DedupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-09 20:58
 */
@RestController
@RequestMapping("/dedup")
public class ContentDedupController {

    private final DedupService dedupService;

    @Autowired
    public ContentDedupController(@Qualifier("contentDedupServiceImpl") DedupService dedupService) {
        this.dedupService = dedupService;
    }

    @GetMapping("/content/id/{id}")
    public List<RelatedDocInfo> dedup(@PathVariable String id) {
        return this.dedupService.dedup(id);
    }

    @PostMapping("/content")
    public RelatedDocs dedup(@RequestBody Doc doc) {
        return this.dedupService.dedup(doc);
    }

}
