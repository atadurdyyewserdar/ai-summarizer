package com.aissummarizer.jennet.dto;

/**
 * Request payload for registering a new user account.
 *
 * @param username the desired username
 * @param password the chosen password
 */
public record RegisterRequest(String username, String password) {}