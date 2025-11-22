package com.aissummarizer.jennet.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents an authenticated user of the system.
 * <p>
 * Contains login credentials, role, and basic profile information.
 */
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
        }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity {

    @Id
    private String id;

    @PrePersist
    public void prePersist() {
        if (this.id == null || this.id.isBlank()) {
            // Generate UUID manually so we don't rely on DB-specific ID generation.
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        this.updatedAt = LocalDateTime.now();
    }

    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Column(nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    /**
     * Application role, e.g. "ROLE_USER" or "ROLE_ADMIN".
     */
    @Column(nullable = false, length = 30)
    private String role;

    // --- Profile fields ---

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(length = 255)
    private String email;

    /**
     * URL or path to the profile picture.
     * <p>
     * We store only a reference (not the binary image) so the actual image
     * can live in S3, local storage, etc.
     */
    @Column(name = "profile_image_url", length = 512)
    private String profileImageUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}