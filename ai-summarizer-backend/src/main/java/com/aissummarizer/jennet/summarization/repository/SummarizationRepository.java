package com.aissummarizer.jennet.summarization.repository;

import com.aissummarizer.jennet.summarization.entity.SummarizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummarizationRepository extends JpaRepository<SummarizationEntity, String> {
    List<SummarizationEntity> findByUserId(String userId);
    List<SummarizationEntity> findByUserIdOrderByCreatedAtDesc(String userId);
}
