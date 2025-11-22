package com.aissummarizer.jennet.summarization.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Default implementation of {@link SummarizationService}.
 */
@Service
@RequiredArgsConstructor
public class SummarizationServiceImpl implements SummarizationService {

    private final SummarizationHistoryRepository repository;

    @Override
    public SummarizationHistoryEntity addEntry(String userId, String inputText, String summaryText) {
        SummarizationHistoryEntity entity = SummarizationHistoryEntity.builder()
                .userId(userId)
                .inputText(inputText)
                .summaryText(summaryText)
                .createdAt(LocalDateTime.now())
                .build();

        return repository.save(entity);
    }

    @Override
    public List<SummarizationHistoryEntity> getHistoryForUser(String userId) {
        return repository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}