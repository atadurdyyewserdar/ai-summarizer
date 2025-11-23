package com.aissummarizer.jennet.summarization.repository;

import com.aissummarizer.jennet.summarization.entity.SummaryResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryResultRepository extends JpaRepository<SummaryResultEntity, String> {
}
