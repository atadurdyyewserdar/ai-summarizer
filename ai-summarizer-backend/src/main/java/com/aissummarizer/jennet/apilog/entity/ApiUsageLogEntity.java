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

    /** HTTP method used (GET, POST, PUT, DELETE...). */
    @Column(nullable = false)
    private String httpMethod;

    @Column(nullable = false)
    private long responseTimeMs;

    /** Size of the request payload in bytes. */
    @Column(name = "request_size_bytes")
    private long requestSizeBytes;

    /** Size of the response payload in bytes. */
    @Column(name = "response_size_bytes")
    private long responseSizeBytes;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private boolean success;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "tokens_used")
    private Integer tokensUsed;

    /** Timestamp of the request. */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** How long the API call took (in milliseconds). */
    @Column(name = "processing_time_ms")
    private long processingTimeMs;
}