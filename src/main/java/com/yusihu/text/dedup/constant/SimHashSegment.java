package com.yusihu.text.dedup.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author yusihu
 * @date 2024-07-09 10:48
 */
@Getter
@AllArgsConstructor
public enum SimHashSegment {
    /**
     * simHash 分为四段
     */
    SEGMENT_A("a"),
    SEGMENT_B("b"),
    SEGMENT_C("c"),
    SEGMENT_D("d");

    private String name;
}
