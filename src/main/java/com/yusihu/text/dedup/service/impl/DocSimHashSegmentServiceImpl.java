package com.yusihu.text.dedup.service.impl;

import com.yusihu.text.dedup.dao.DocSimHashSegmentRepository;
import com.yusihu.text.dedup.entity.DocSimHashSegment;
import com.yusihu.text.dedup.entity.DocSimHashSegment.DocSimHash;
import com.yusihu.text.dedup.service.DocSimHashSegmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yusihu
 * @date 2024-07-09 15:09
 */
@Service
@Slf4j
public class DocSimHashSegmentServiceImpl implements DocSimHashSegmentService {

    private DocSimHashSegmentRepository repository;

    @Autowired
    public DocSimHashSegmentServiceImpl(DocSimHashSegmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public DocSimHashSegment search(String simHashSegment) {
        try {
            return this.repository.findById(simHashSegment).orElse(null);
        } catch (Exception e) {
            log.error("search docSimHash error, simHashSegment=" + simHashSegment, e);
        }
        return null;
    }

    @Override
    public List<DocSimHash> searchDocSimHashes(String simHashSegment) {

        try {
            final DocSimHashSegment docSimHashSegment = this.repository.findById(simHashSegment).orElse(null);
            if (docSimHashSegment == null) {
                return new ArrayList<>();
            } else {
                return docSimHashSegment.getDocSimHashes();
            }
        } catch (Exception e) {
            log.error("search docSimHash error, simHashSegment=" + simHashSegment, e);
        }
        return null;
    }

    @Override
    public void save(DocSimHashSegment simHashSegment) {

        try {
            this.repository.save(simHashSegment);
        } catch (Exception e) {
            log.error("saveDocSimHashSegment error, segment=" + simHashSegment.getId() + ", docHashs=" + simHashSegment.getDocSimHashes());
        }
    }
}
