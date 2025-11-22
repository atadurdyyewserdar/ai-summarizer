package com.aissummarizer.jennet.apilog.entity;

import com.aissummarizer.jennet.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Tracks user API usage for audit, analytics, or rate limiting.
 */
@Entity
@Table(name = "api_usage_logs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class ApiUsageLogEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @PrePersist
    public void ensureId() {
        if (id == null) id = UUID.randomUUID().toString();
    }

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(nullable = false, length = 100)
    private String endpoint;

    @Column(nullable = false)
    private long responseTimeMs;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean success;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;
}