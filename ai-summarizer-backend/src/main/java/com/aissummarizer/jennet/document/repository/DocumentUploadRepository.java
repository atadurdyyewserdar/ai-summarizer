package com.aissummarizer.jennet.document.repository;

import com.aissummarizer.jennet.document.entity.DocumentUploadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentUploadRepository extends JpaRepository<DocumentUploadEntity, String> {
    List<DocumentUploadEntity> findByUserId(String userId);
}
