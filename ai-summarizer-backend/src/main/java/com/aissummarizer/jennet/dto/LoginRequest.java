package com.aissummarizer.jennet.dto;


/**
 * Request payload for logging in a user.
 *
 * @param username the user's identifier
 * @param password the user's unencrypted password
 */
public record LoginRequest(String username, String password) {}