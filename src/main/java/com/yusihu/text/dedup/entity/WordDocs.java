package com.yusihu.text.dedup.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-10 10:21
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("word_docs")
public class WordDocs {
    @Id
    private String word;
    private List<WordDocInfo> docInfos;

    @Setter
    @Getter
    @AllArgsConstructor
    public static class WordDocInfo {
        private String id;
        private String title;

    }
}
