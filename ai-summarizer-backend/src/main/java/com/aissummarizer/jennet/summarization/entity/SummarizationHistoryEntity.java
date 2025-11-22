package com.aissummarizer.jennet.summarization.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents a single AI summarization performed by a user.
 * <p>
 * Stores the original text and the generated summary, along with timestamp.
 */
@Entity
@Table(name = "summarization_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SummarizationHistoryEntity {

    @Id
    @Column(length = 36)
    private String id;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    /**
     * ID of the user who performed this summarization.
     * <p>
     * We intentionally do NOT map this as a foreign key (@ManyToOne) to avoid
     * SQLite "ALTER TABLE ADD CONSTRAINT" issues.
     */
    @Column(name = "user_id", nullable = false, length = 36)
    private String userId;

    @Lob
    @Column(name = "input_text", nullable = false)
    private String inputText;

    @Lob
    @Column(name = "summary_text", nullable = false)
    private String summaryText;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
