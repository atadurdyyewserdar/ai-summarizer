package com.aissummarizer.jennet.user.entity;
import com.aissummarizer.jennet.common.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

/**
 * Represents an application user.
 * Stores authentication data and profile details.
 */
@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @PrePersist
    public void ensureId() {
        if (id == null) id = UUID.randomUUID().toString();
    }

    @Column(name = "user_name", nullable = false, unique = true, length = 120)
    private String userName;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "email", nullable = false, unique = true, length = 180)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "first_name", length = 60)
    private String firstName;

    @Column(name = "last_name", length = 60)
    private String lastName;

    @Column(length = 500)
    private String profileImageUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    @JsonFormat(pattern = "MMM dd, yyyy 'on' HH:mm", locale = "en")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @JsonFormat(pattern = "MMM dd, yyyy 'on' HH:mm", locale = "en")
    private LocalDateTime updatedAt;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "refresh_token_expires_at")
    private LocalDateTime refreshTokenExpiresAt;

}