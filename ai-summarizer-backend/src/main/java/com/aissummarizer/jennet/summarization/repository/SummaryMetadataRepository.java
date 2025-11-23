package com.aissummarizer.jennet.summarization.repository;

import com.aissummarizer.jennet.summarization.entity.SummaryMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SummaryMetadataRepository extends JpaRepository<SummaryMetadataEntity, String> {
}
