package com.aissummarizer.jennet.model.response;

import com.aissummarizer.jennet.model.enums.SummaryType;
import com.aissummarizer.jennet.model.request.DocumentType;

import java.util.Objects;

/**
 * Immutable summary result
 */
public final class SummaryResult {
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

    // Getters
    public String getSummary() { return summary; }
    public DocumentType getDocumentType() { return documentType; }
    public SummaryType getSummaryType() { return summaryType; }
    public SummaryMetadata getMetadata() { return metadata; }

    @Override
    public String toString() {
        return String.format("SummaryResult[type=%s, words=%d]",
                documentType, metadata.getWordCount());
    }
}
