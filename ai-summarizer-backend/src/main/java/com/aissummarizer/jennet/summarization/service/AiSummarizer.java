package com.aissummarizer.jennet.summarization.service;

import com.aissummarizer.jennet.common.exception.AiSummarizationException;
import com.aissummarizer.jennet.document.service.DocumentContent;
import com.aissummarizer.jennet.summarization.model.SummaryOptions;
import com.aissummarizer.jennet.summarization.model.SummaryResult;

/**
 * Strategy pattern for AI summarization
 */
public interface AiSummarizer {
    /**
     * Summarize document content
     * @param content Document content to summarize
     * @param options Summarization options
     * @return Summary result
     * @throws AiSummarizationException if summarization fails
     */
    SummaryResult summarize(DocumentContent content, SummaryOptions options)
            throws AiSummarizationException;
}
