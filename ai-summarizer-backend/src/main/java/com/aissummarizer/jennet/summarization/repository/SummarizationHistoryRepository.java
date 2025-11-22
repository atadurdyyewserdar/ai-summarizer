package com.aissummarizer.jennet.summarization.repository;

import com.aissummarizer.jennet.summarization.entity.SummarizationHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Persistence abstraction for summarization history records.
 */
public interface SummarizationHistoryRepository
        extends JpaRepository<SummarizationHistoryEntity, String> {

    List<SummarizationHistoryEntity> findByUserIdOrderByCreatedAtDesc(String userId);
}