package com.aissummarizer.jennet.dto;

/**
 * Request object for initiating a password reset.
 * <p>
 * Contains the username of the user requesting a password reset.
 */
public record ForgotPasswordRequest(String username) { }