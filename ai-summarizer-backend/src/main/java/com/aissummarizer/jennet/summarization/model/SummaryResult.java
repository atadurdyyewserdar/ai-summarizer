package com.aissummarizer.jennet.summarization.model;

import com.aissummarizer.jennet.summarization.enums.SummaryType;
import com.aissummarizer.jennet.document.enums.DocumentType;
import lombok.Getter;

import java.util.Objects;

/**
 * Immutable summary result
 */
@Getter
public final class SummaryResult {
    // Getters
    private final String summary;
    private final DocumentType documentType;
    private final SummaryType summaryType;
    private final SummaryMetadata metadata;

    public SummaryResult(String summary, DocumentType documentType,
                         SummaryType summaryType, SummaryMetadata metadata) {
        this.summary = Objects.requireNonNull(summary, "summary cannot be null");
        this.documentType = Objects.requireNonNull(documentType, "documentType cannot be null");
        this.summaryType = Objects.requireNonNull(summaryType, "summaryType cannot be null");
        this.metadata = Objects.requireNonNull(metadata, "metadata cannot be null");
    }

    @Override
    public String toString() {
        return String.format("SummaryResult[type=%s, words=%d]",
                documentType, metadata.getWordCount());
    }
}
