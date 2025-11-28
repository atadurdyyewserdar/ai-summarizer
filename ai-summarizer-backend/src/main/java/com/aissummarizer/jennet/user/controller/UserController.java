package com.aissummarizer.jennet.user.controller;

import com.aissummarizer.jennet.common.model.ApiResponse;
import com.aissummarizer.jennet.common.model.HttpResponse;
import com.aissummarizer.jennet.user.dto.UserProfileDto;
import com.aissummarizer.jennet.user.dto.UserProfileImageDto;
import com.aissummarizer.jennet.user.dto.UserProfileResponse;
import com.aissummarizer.jennet.user.dto.UserSummarizationHistoryResponse;
import com.aissummarizer.jennet.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
     * @param username is username or email
     * @return User profile response DTO (name, lastname, email, profile image, role, etc.)
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(@RequestParam String username) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserProfile(username)));
    }

    @PostMapping("/update-profile")
    public ResponseEntity<String> updateProfile(@RequestBody UserProfileDto userProfileDto) {
        return ResponseEntity.ok(userService.updateProfile(userProfileDto));
    }

    @DeleteMapping("/delete-summarization")
    public ResponseEntity<String>deleteSummarization (@RequestParam String summaryId) {
        userService.deleteSummary(summaryId);
        return ResponseEntity.ok("Success");
    }

    @GetMapping("/get-users")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<UserProfileDto>> getAllUsers() {
        List<UserProfileDto> profileDtos = userService.getAllUsers();
        return ResponseEntity.ok(profileDtos);
    }
}