package com.yusihu.text.dedup.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

/**
 * @author yusihu
 * @date 2024-07-05 10:51
 */

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "document")
public class Doc {

    @Id
    private String id;

    private String title;

    private String content;

    private BigInteger contentHash;


}
