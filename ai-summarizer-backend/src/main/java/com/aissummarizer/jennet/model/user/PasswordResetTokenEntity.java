package com.aissummarizer.jennet.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a password reset token assigned to a specific user.
 * <p>
 * A token is used as a temporary credential that allows a user to
 * securely reset their password. Tokens have an expiration time
 * and are marked as "used" once consumed to prevent replay attacks.
 */
//@Entity
//@Table(name = "password_reset_tokens")
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class PasswordResetTokenEntity {
//
//    @Id
//    @Column(length = 36)
//    private String id;
//
//    @PrePersist
//    private void generateIdIfMissing() {
//        if (id == null) {
//            id = java.util.UUID.randomUUID().toString();
//        }
//    }
//
//    @Column(nullable = false, unique = true, length = 200)
//    private String token;
//
//    /**
//     * For SQLite compatibility:
//     * - Use TEXT column instead of constraint-based FK
//     * - You may manually validate relationships in service layer
//     */
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id") // OK for SQLite as long as you do NOT enable FOREIGN_KEYS pragma
//    private UserEntity user;
//
//    @Column(nullable = false)
//    private LocalDateTime expiresAt;
//
//    @Column(nullable = false)
//    private boolean used;
//
//    @Column(nullable = false)
//    private LocalDateTime createdAt;
//}

/**
 * Entity representing a password reset token.
 * <p>
 * In this SQLite setup we store only the userId as a String instead of a
 * true foreign key constraint, because Hibernate's schema update generates
 * ALTER TABLE ... ADD CONSTRAINT statements that SQLite does not support.
 */
@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetTokenEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 200)
    private String token;

    /**
     * ID of the user this token belongs to.
     * We keep it as a simple column (no DB-level FK) to avoid SQLite DDL issues.
     */
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    private LocalDateTime createdAt;
}