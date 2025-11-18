package com.aissummarizer.jennet.service;

import com.aissummarizer.jennet.exceptions.AiSummarizationException;
import com.aissummarizer.jennet.model.domain.DocumentContent;
import com.aissummarizer.jennet.model.request.SummaryOptions;
import com.aissummarizer.jennet.model.response.SummaryResult;

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
