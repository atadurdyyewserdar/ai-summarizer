package com.aissummarizer.jennet.repository;
import com.aissummarizer.jennet.model.user.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing UserEntity persistence.
 * <p>
 * Spring Data JPA automatically implements common query operations.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, String> {

    /**
     * Retrieves a user by username, if present.
     *
     * @param username the username to look up
     * @return an optional containing the user or empty if not found
     */
    Optional<UserEntity> findByUsername(String username);
}