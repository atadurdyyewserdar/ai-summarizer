package com.aissummarizer.jennet.summarization.entity;

import com.aissummarizer.jennet.document.entity.DocumentUploadEntity;
import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.summarization.enums.SummaryType;
import com.aissummarizer.jennet.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a single summarization request performed by a user.
 */
@Entity
@Table(name = "summarizations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SummarizationEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @PrePersist
    public void ensureId() {
        if (id == null) id = UUID.randomUUID().toString();
    }

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "input_text", columnDefinition = "text", nullable = false)
    private String inputText;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "timestamp with time zone")
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "summary_type", nullable = false, length = 30)
    private SummaryType summaryType;

    /**
     * One summarization → one result.
     */
    @OneToOne(mappedBy = "summarization", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SummaryResultEntity result;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_upload_id")
    private DocumentUploadEntity documentUpload;

    @Column(name = "summary_text", columnDefinition = "text")
    private String summaryText;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", length = 20, nullable = false)
    private DocumentType documentType;

    @OneToOne(mappedBy = "summarization", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SummaryMetadataEntity metadata;

}
