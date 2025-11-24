package com.aissummarizer.jennet.summarization.model;

import com.aissummarizer.jennet.summarization.entity.SummarizationEntity;
import com.aissummarizer.jennet.summarization.entity.SummaryMetadataEntity;
import com.aissummarizer.jennet.summarization.entity.SummaryResultEntity;

public record SummaryClasses(SummarizationEntity summarization, SummaryMetadataEntity metadataEntity, SummaryResultEntity summaryResultEntity) {
}
