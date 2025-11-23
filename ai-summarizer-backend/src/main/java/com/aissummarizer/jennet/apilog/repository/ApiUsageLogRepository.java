package com.aissummarizer.jennet.apilog.repository;

import com.aissummarizer.jennet.apilog.entity.ApiUsageLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for API usage logging.
 */
public interface ApiUsageLogRepository extends JpaRepository<ApiUsageLogEntity, String> {
}
