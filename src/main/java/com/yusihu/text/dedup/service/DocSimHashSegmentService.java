package com.yusihu.text.dedup.service;

import com.yusihu.text.dedup.entity.DocSimHashSegment;
import com.yusihu.text.dedup.entity.DocSimHashSegment.DocSimHash;

import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-09 15:09
 */
public interface DocSimHashSegmentService {

    DocSimHashSegment search(String simHashSegment);

    List<DocSimHash> searchDocSimHashes(String simHashSegment);

    void save(DocSimHashSegment simHashSegment);
}
