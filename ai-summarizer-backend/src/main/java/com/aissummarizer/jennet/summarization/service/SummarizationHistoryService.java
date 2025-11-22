package com.aissummarizer.jennet.summarization.service;


import com.aissummarizer.jennet.summarization.entity.SummarizationHistoryEntity;

import java.util.List;

/**
 * Business operations around storing and retrieving summarization history.
 */
public interface SummarizationHistoryService {

    SummarizationHistoryEntity addEntry(String userId, String inputText, String summaryText);

    List<SummarizationHistoryEntity> getHistoryForUser(String userId);
}
