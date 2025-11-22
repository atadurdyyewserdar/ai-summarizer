package com.aissummarizer.jennet.summarization.repository;

import com.aissummarizer.jennet.summarization.entity.SummarizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SummarizationRepository extends JpaRepository<SummarizationEntity, String> {

    List<SummarizationEntity> findByUserId(String userId);
}
