package com.aissummarizer.jennet.user.entity;

import com.aissummarizer.jennet.summarization.entity.SummarizationEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents an authenticated user of the system.
 * <p>
 * Contains login credentials, role, and basic profile information.
 */
//@Entity
//@Table(
//        name = "users",
//        uniqueConstraints = {
//                @UniqueConstraint(name = "uk_users_username", columnNames = "username"),
//                @UniqueConstraint(name = "uk_users_email", columnNames = "email")
//        }
//)
//@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
//public class UserEntity {
//
//    @Id
//    private String id;
//
//    @PrePersist
//    public void prePersist() {
//        if (this.id == null || this.id.isBlank()) {
//            // Generate UUID manually so we don't rely on DB-specific ID generation.
//            this.id = java.util.UUID.randomUUID().toString();
//        }
//        if (this.createdAt == null) {
//            this.createdAt = LocalDateTime.now();
//        }
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    public void preUpdate() {
//        this.updatedAt = LocalDateTime.now();
//    }
//
//    @Column(nullable = false, length = 100)
//    private String username;
//
//    @Column(nullable = false)
//    private String password;
//
//    /**
//     * Application role, e.g. "ROLE_USER" or "ROLE_ADMIN".
//     */
//    @Column(nullable = false, length = 30)
//    private String role;
//
//    // --- Profile fields ---
//
//    @Column(name = "first_name", length = 100)
//    private String firstName;
//
//    @Column(name = "last_name", length = 100)
//    private String lastName;
//
//    @Column(length = 255)
//    private String email;
//
//    /**
//     * URL or path to the profile picture.
//     * <p>
//     * We store only a reference (not the binary image) so the actual image
//     * can live in S3, local storage, etc.
//     */
//    @Column(name = "profile_image_url", length = 512)
//    private String profileImageUrl;
//
//    @Column(name = "created_at", nullable = false)
//    private LocalDateTime createdAt;
//
//    @Column(name = "updated_at", nullable = false)
//    private LocalDateTime updatedAt;
//}


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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

    @Column(nullable = false, unique = true, length = 120)
    private String username;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    @Column(nullable = false)
    private String role; // e.g. ROLE_USER, ROLE_ADMIN

    @Column(length = 60)
    private String firstName;

    @Column(length = 60)
    private String lastName;

    @Column(length = 500)
    private String profileImageUrl;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * One user → many summarizations.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SummarizationEntity> summarizations = new ArrayList<>();
}