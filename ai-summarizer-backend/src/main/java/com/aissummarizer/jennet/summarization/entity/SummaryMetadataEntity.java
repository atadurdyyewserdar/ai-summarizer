package com.aissummarizer.jennet.summarization.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Stores detailed analytics about the summarization result.
 */
@Entity
@Table(name = "summary_metadata")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class SummaryMetadataEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @PrePersist
    public void ensureId() {
        if (id == null) id = UUID.randomUUID().toString();
    }

    @OneToOne(optional = false)
    @JoinColumn(name = "summary_result_id", nullable = false)
    private SummaryResultEntity summaryResult;

    private int wordCount;
    private int imageCount;
    private int slideCount;
    private int paragraphCount;
    private int tableCount;

    /**
     * Processing time in milliseconds.
     */
    private long processingTime;
}
