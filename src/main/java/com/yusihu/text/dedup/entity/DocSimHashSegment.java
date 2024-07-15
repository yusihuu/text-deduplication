package com.yusihu.text.dedup.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-08 10:14
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Document("doc_sim_hash_segment")
public class DocSimHashSegment {
    @Id
    private String id;

    private List<DocSimHash> docSimHashes;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DocSimHash {

        private String id;
        private BigInteger contentHash;

    }
}
