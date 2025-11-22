package com.aissummarizer.jennet.user.controller;

import com.aissummarizer.jennet.user.dto.UserProfileResponse;
import com.aissummarizer.jennet.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * User controller handling authenticated user actions such as retrieving profile data.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Returns the authenticated user's profile details.
     * <p>
     * The username is extracted from the Authentication object injected by Spring Security after
     * successful JWT validation in JwtAuthFilter.
     *
     * @param authentication Spring Security authentication context
     * @return User profile response DTO (name, lastname, email, profile image, role, etc.)
     */
    @GetMapping("/profile")
    public UserProfileResponse getProfile(Authentication authentication) {
        String username = authentication.getName();
        return userService.getUserProfile(username);
    }
}