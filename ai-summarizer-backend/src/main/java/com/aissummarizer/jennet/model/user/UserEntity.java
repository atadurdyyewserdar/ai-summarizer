package com.aissummarizer.jennet.model.user;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

///**
// * Represents an application user stored in the database.
// * <p>
// * This entity is used for authentication, authorization, and all
// * user-related domain operations. Passwords stored here are always
// * encoded (hashed) using a PasswordEncoder implementation.
// */
//@Entity
//@Table(name = "users")
//@Data
//@Builder
//public class UserEntity {
//
//    /**
//     * Primary identifier stored as UUID.
//     * <p>
//     * UUID is used instead of numeric IDs to avoid predictable sequences.
//     */
//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    private String id;
//
//    /**
//     * Unique username used for login.
//     * <p>
//     * This field could represent email if the system uses email logins.
//     */
//    @Column(unique = true, nullable = false, length = 100)
//    private String username;
//
//    /**
//     * Encoded password (BCrypt or Argon2).
//     * <p>
//     * Never store raw passwords in the database.
//     */
//    @Column(nullable = false)
//    private String password;
//
//    /**
//     * User role used for authorization checks.
//     * <p>
//     * Stored as "ROLE_USER", "ROLE_ADMIN", etc.
//     */
//    @Column(nullable = false, length = 30)
//    private String role;
//}
//
//@Entity
//@Table(
//        name = "users",
//        // SQLite can't handle named constraints. Only use implicit unique = true.
//        uniqueConstraints = {
//                @UniqueConstraint(columnNames = {"username"})
//        }
//)
//@Data
//@Builder
//public class UserEntity {
//
//    /**
//     * SQLite does not support UUID generation natively in DDL.
//     * Hibernate's UUID generation works fine.
//     */
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
//    /**
//     * UNIQUE works in SQLite *only* if defined at the column level,
//     * NOT as a named constraint.
//     */
//    @Column(nullable = false, unique = true, length = 200)
//    private String username;
//
//    /**
//     * SQLite does not enforce length, but Hibernate will generate TEXT/VARCHAR correctly.
//     */
//    @Column(nullable = false, length = 200)
//    private String password;
//
//    @Column(nullable = false, length = 50)
//    private String role;
//}

///**
// * User entity compatible with SQLite.
// * SQLite has limited DDL syntax, so only allow simple NOT NULL + UNIQUE constraints.
// * Avoid: CHECK, column-level constraint keyword, advanced validation annotations.
// */
//@Entity
//@Table(
//        name = "users",
//        uniqueConstraints = {
//                @UniqueConstraint(name = "uk_users_username", columnNames = "username")
//        }
//)
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class UserEntity {
//
//    /**
//     * Primary key.
//     * SQLite does NOT support UUID generation natively.
//     * Using TEXT with manual UUID generation (in service layer).
//     */
//    @Id
//    @Column(length = 36)
//    private String id;
//
//    @Column(nullable = false, length = 100)
//    private String username;
//
//    @Column(nullable = false)
//    private String password;
//
//    /**
//     * Simple role column.
//     * No enums — SQLite does not support enum type.
//     */
//    @Column(nullable = false, length = 30)
//    private String role;
//}


@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity {

    @Id
    private String id;

    @PrePersist
    public void generateId() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString(); // SQLite-friendly PK
        }
    }

    @Column(unique = true, nullable = false, length = 100)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 30)
    private String role;
}