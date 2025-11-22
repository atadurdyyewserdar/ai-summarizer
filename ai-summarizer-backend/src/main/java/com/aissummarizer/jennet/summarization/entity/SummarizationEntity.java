package com.aissummarizer.jennet.summarization.entity;

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

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String inputText;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SummaryType summaryType;

    /**
     * One summarization → one result.
     */
    @OneToOne(mappedBy = "summarization", cascade = CascadeType.ALL, orphanRemoval = true)
    private SummaryResultEntity result;
}
