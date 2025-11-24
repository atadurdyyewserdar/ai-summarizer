package com.aissummarizer.jennet.user.service;

import com.aissummarizer.jennet.auth.dto.RegisterRequest;
import com.aissummarizer.jennet.user.dto.UserProfileDto;
import com.aissummarizer.jennet.user.dto.UserProfileResponse;
import com.aissummarizer.jennet.user.entity.UserEntity;

import java.util.Optional;

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
    UserEntity registerUser(RegisterRequest registerRequest);

    /**
     * Retrieves a user by username.
     *
     * @param username username to look up
     * @return the matching UserEntity
     */
    UserEntity getByUserName(String username);

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

    /**
     * Generates UserProfileResponse DTO including
     * profile info + summarization stats.
     *
     * @param userName username
     * @return populated profile response
     */
    UserProfileResponse getUserProfile(String userName);

    /**
     * Updates user's profile info (name, lastname, image, email).
     */
    String updateProfile(UserProfileDto userProfileDto);

    void saveRefreshToken(String userId, String refreshToken);

    Optional<UserEntity> findByRefreshToken(String refreshToken);
}