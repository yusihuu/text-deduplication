package com.yusihu.text.dedup.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author yusihu
 * @date 2024-07-07 18:19
 */
@Setter
@Getter
@AllArgsConstructor
@Document(collection = "related_docs")
public class RelatedDocs {

    @Id
    private String id;

    private List<RelatedDocInfo> relatedDocInfos;

    public void merge(RelatedDocs other) {
        if (other == null || CollectionUtils.isEmpty(other.getRelatedDocInfos())) {
            return;
        }
        Set<String> temp = this.relatedDocInfos.stream().map(RelatedDocInfo::getId).collect(Collectors.toSet());
        for (RelatedDocInfo docInfo : other.getRelatedDocInfos()) {
            String id = docInfo.getId();
            if (temp.contains(id)) {
                continue;
            }
            temp.add(id);
            this.relatedDocInfos.add(docInfo);
        }
        Collections.sort(this.relatedDocInfos);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class RelatedDocInfo implements Comparable<RelatedDocInfo>{
        private String id;
        private Double distance;

        @Override
        public int compareTo(RelatedDocInfo other) {
            return -Double.compare(this.distance, other.distance);
        }
    }
}
