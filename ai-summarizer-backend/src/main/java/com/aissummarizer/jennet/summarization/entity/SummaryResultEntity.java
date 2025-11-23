package com.aissummarizer.jennet.summarization.entity;

import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.summarization.enums.SummaryType;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Stores the actual summary produced by the AI engine.
 */
@Entity
@Table(name = "summary_results")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SummaryResultEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @PrePersist
    public void ensureId() {
        if (id == null) id = UUID.randomUUID().toString();
    }

    @OneToOne(optional = false)
    @JoinColumn(name = "summarization_id", nullable = false)
    private SummarizationEntity summarization;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "summary_type", length = 30, nullable = false)
    private SummaryType summaryType;

    /**
     * One result → one metadata record.
     */
    @OneToOne(mappedBy = "summaryResult", cascade = CascadeType.ALL, orphanRemoval = true)
    private SummaryMetadataEntity metadata;
}