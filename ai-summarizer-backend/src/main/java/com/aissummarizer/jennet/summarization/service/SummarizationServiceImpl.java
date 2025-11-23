package com.aissummarizer.jennet.summarization.service;

import com.aissummarizer.jennet.document.entity.DocumentUploadEntity;
import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.summarization.entity.SummarizationEntity;
import com.aissummarizer.jennet.summarization.entity.SummaryMetadataEntity;
import com.aissummarizer.jennet.summarization.entity.SummaryResultEntity;
import com.aissummarizer.jennet.summarization.enums.SummaryType;
import com.aissummarizer.jennet.summarization.repository.SummarizationRepository;
import com.aissummarizer.jennet.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Default implementation of {@link SummarizationService}.
 */
@Service
@RequiredArgsConstructor
public class SummarizationServiceImpl implements SummarizationService {
    private final SummarizationRepository summarizationRepository;

    /**
     * Creates and saves a full summarization record.
     * <p>
     * This is typically called AFTER the AI summarizer finishes,
     * when you have both summary text and derived metadata.
     */
    public SummarizationEntity saveSummarization(UserEntity user,
                                                 DocumentUploadEntity documentUpload,
                                                 String inputText,
                                                 String summaryText,
                                                 DocumentType documentType,
                                                 SummaryType summaryType,
                                                 int wordCount,
                                                 int imageCount,
                                                 int slideCount,
                                                 int paragraphCount,
                                                 int tableCount,
                                                 long processingTimeMs) {

        SummarizationEntity summarization = new SummarizationEntity();
        summarization.setId(UUID.randomUUID().toString());
        summarization.setUser(user);
        summarization.setDocumentUpload(documentUpload);
        summarization.setInputText(inputText);
        summarization.setSummaryText(summaryText);
        summarization.setDocumentType(documentType);
        summarization.setSummaryType(summaryType);
        summarization.setCreatedAt(LocalDateTime.now());

        SummaryMetadataEntity metadata = new SummaryMetadataEntity();
        metadata.setId(UUID.randomUUID().toString());
        metadata.setSummarization(summarization);
        metadata.setWordCount(wordCount);
        metadata.setImageCount(imageCount);
        metadata.setSlideCount(slideCount);
        metadata.setParagraphCount(paragraphCount);
        metadata.setTableCount(tableCount);
        metadata.setProcessingTime(processingTimeMs);

        SummaryResultEntity result = new SummaryResultEntity();
        result.setId(UUID.randomUUID().toString());
        result.setSummarization(summarization);
        result.setSummary(summaryText);
        result.setDocumentType(documentType);
        result.setSummaryType(summaryType);
        summarization.setMetadata(metadata);
        summarization.setResult(result);

        // 🔐 One save cascades to metadata & result because of CascadeType.ALL
        return summarizationRepository.save(summarization);
    }

    @Override
    public SummarizationEntity saveSummarization(SummarizationEntity summarization) {
        return summarizationRepository.save(summarization);
    }

    @Override
    public List<SummarizationEntity> findByUserId(String userId) {
        return summarizationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}