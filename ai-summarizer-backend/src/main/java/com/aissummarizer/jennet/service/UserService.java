package com.aissummarizer.jennet.service;

import com.aissummarizer.jennet.model.user.UserEntity;

/**
 * Defines the business operations related to users.
 * <p>
 * This layer separates domain logic from controllers and repositories,
 * making it easier to test and maintain.
 */
public interface UserService {

    /**
     * Registers a new user with the given username and raw password.
     *
     * @param username   desired unique username
     * @param rawPassword plain text password (will be encoded)
     * @return persisted user
     */
    UserEntity registerUser(String username, String rawPassword);

    /**
     * Retrieves a user by username.
     *
     * @param username username to look up
     * @return the matching UserEntity
     */
    UserEntity getByUsername(String username);

    /**
     * Retrieves a user by ID.
     *
     * @param id unique identifier
     * @return the matching UserEntity
     */
    UserEntity getById(String id);

    /**
     * Changes the password for a given user.
     *
     * @param userId        ID of the user
     * @param rawNewPassword new password before encoding
     */
    void changePassword(String userId, String rawNewPassword);
}