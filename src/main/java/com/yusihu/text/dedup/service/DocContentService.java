package com.yusihu.text.dedup.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yusihu.text.dedup.http.HttpDelegator;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * @author yusihu
 * @date 2024-07-04 20:29
 */
@Slf4j
public class DocContentService {

    private static final HttpDelegator DELEGATOR = new HttpDelegator(500);
    private static final String URL = "http://contech.markthal-uuid.int.yidian-inc.com/markthal/docIds/%s/fields/parse_title,text_content";
    private static final String PARSE_TITLE = "parse_title";
    private static final String TEXT_CONTENT = "text_content";

    public Map<String, String> getTileAndContent(String docId) {
        Map<String, String> data = Maps.newHashMap();
        try {
            // 通过内容侧接口获取数据
            String resp = DELEGATOR.get(String.format(URL, docId));
            final JSONObject jsonObject = JSONObject.parseObject(resp);
            if (null == jsonObject) {
                return data;
            }
            final JSONArray result = jsonObject.getJSONArray("result");
            if (null == result || result.size() == 0) {
                return data;
            }
            final JSONObject resultJsonObject = result.getJSONObject(0);
            if (null == resultJsonObject) {
                return data;
            }
            // 获取标题、文章
            final String parseTitle = resultJsonObject.getString(PARSE_TITLE);
            final String textContent = resultJsonObject.getString(TEXT_CONTENT);

            // 封装到map
        } catch (Exception e) {
            log.error("get document error, docId=" + docId + ", errorMsg=" + e.getMessage(), e);
        }

        return data;
    }

    public static void main(String[] args) {
        DocContentService docContentService = new DocContentService();
        final Map<String, String> tileAndContent = docContentService.getTileAndContent("0vFqFpDP");
    }
}
