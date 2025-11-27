package com.aissummarizer.jennet.summarization.service;

import com.aissummarizer.jennet.summarization.entity.SummarizationEntity;

import java.util.List;

/**
 * Business operations around storing and retrieving summarization history.
 */
public interface SummarizationService {
    public SummarizationEntity saveSummarization(SummarizationEntity summarization);

    public List<SummarizationEntity> findByUserId(String userId);

    void deleteById(String summaryId);
}
