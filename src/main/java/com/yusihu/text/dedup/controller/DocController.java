package com.yusihu.text.dedup.controller;

import com.yusihu.text.dedup.entity.Doc;
import com.yusihu.text.dedup.service.DocService;
import com.yusihu.text.dedup.service.impl.DocHashServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-07 17:08
 */
@RestController
@RequestMapping("/doc")
public class DocController {

    private DocService docService;
    private final DocHashServiceImpl docHashService;

    @Autowired
    public DocController(DocService docService, DocHashServiceImpl docHashService) {
        this.docService = docService;
        this.docHashService = docHashService;
    }

    @PostMapping("/add")
    public void save(@RequestBody Doc doc) {
        this.docHashService.save(doc);
    }

    @GetMapping("/get/{id}")
    public Doc getDocById(@PathVariable String id) {
        return this.docService.findById(id);
    }

    @GetMapping("/getAll")
    public List<Doc> getAllDoc() {
        return this.docService.findAll();
    }
}
