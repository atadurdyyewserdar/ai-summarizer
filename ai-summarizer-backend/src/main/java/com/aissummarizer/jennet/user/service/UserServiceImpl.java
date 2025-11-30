package com.aissummarizer.jennet.user.service;

import com.aissummarizer.jennet.auth.dto.RegisterRequest;
import com.aissummarizer.jennet.common.enums.Role;
import com.aissummarizer.jennet.common.exception.BadRequestException;
import com.aissummarizer.jennet.common.exception.NotFoundException;
import com.aissummarizer.jennet.summarization.entity.SummarizationEntity;
import com.aissummarizer.jennet.summarization.service.SummarizationService;
import com.aissummarizer.jennet.user.dto.UpdateDto;
import com.aissummarizer.jennet.user.dto.UserProfileDto;
import com.aissummarizer.jennet.user.dto.UserProfileResponse;
import com.aissummarizer.jennet.user.dto.UserSummarizationHistoryResponse;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of the UserService interface.
 * <p>
 * Handles high-level user operations such as registration and password updates.
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final SummarizationService summarizationService;


    /**
     * Returns the user's profile information including a summarized view of
     * their summarization history.
     *
     * @param userName username of the user whose profile is being fetched
     * @return a DTO containing basic user profile information + history stats
     */
    @Override
    public UserProfileResponse getUserProfile(String userName) {

        // Fetch user
        UserEntity user = getByUserName(userName);

        // Fetch summarization history belonging to this user
        List<SummarizationEntity> history =
                summarizationService.findByUserId(user.getId());

        System.out.println("SUMMARIZATION HISTORY:");
        System.out.println(history.toString());

        // Convert each entity → DTO (never expose entities directly)
        List<UserSummarizationHistoryResponse> historyDtos = history.stream()
                .map(h -> new UserSummarizationHistoryResponse(
                        h.getId(),
                        h.getCreatedAt(),
                        h.getResult().getSummary(),
                        h.getSummaryType().toString(),
                        h.getDocumentType().getExtension(),
                        h.getDocumentUpload().getOriginalFilename(),
                        h.getMetadata().getImageCount(),
                        h.getMetadata().getParagraphCount(),
                        h.getMetadata().getSlideCount(),
                        h.getMetadata().getProcessingTime(),
                        h.getMetadata().getTableCount(),
                        h.getMetadata().getWordCount(),
                        h.getDocumentUpload().getFileSize()
                        ))
                .toList();

        // Build final profile response
        return new UserProfileResponse(
                user.getId(),
                user.getUserName(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getProfileImageUrl(),
                user.getCreatedAt(),
                user.getUpdatedAt(),
                user.getRole(),
                historyDtos
        );
    }


    @Override
    public UserEntity registerUser(RegisterRequest registerRequest) {

        userRepository.findByUserName(registerRequest.username()).ifPresent(u -> {
            throw new BadRequestException("Username already exists");
        });

        userRepository.findByEmail(registerRequest.email()).ifPresent(u -> {
            throw new BadRequestException("Email already registered");
        });

        UserEntity user = UserEntity.builder()
                .userName(registerRequest.username())
                .password(encoder.encode(registerRequest.password()))
                .email(registerRequest.email())
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .role(Role.ROLE_USER)
                .profileImageUrl(null)
                .build();

        return userRepository.save(user);
    }

    @Override
    public UserEntity getByUserName(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public UserEntity getById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public void changePassword(String userName, String rawNewPassword) {
        UserEntity user = getByUserName(userName);
        user.setPassword(encoder.encode(rawNewPassword));
        userRepository.save(user);
    }

    @Override
    public String updateProfile(UpdateDto dto) {
        log.info(dto.email());
        log.info(dto.firstName());
        log.info(dto.lastName());
        log.info(dto.username());
        UserEntity user = getByUserName(dto.username());

        if (!dto.firstName().isBlank()) {
            user.setFirstName(dto.firstName());
        }
        if (!dto.lastName().isBlank()) {
            user.setLastName(dto.lastName());
        }
        if (!dto.email().isBlank()) {
            user.setEmail(dto.email());
        }
        userRepository.save(user);
        return "Success";
    }

    @Override
    public void saveRefreshToken(String userId, String refreshToken) {
        UserEntity user = getById(userId);
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiresAt(LocalDateTime.now().plusDays(7));
        userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findByRefreshToken(String refreshToken) {
        return userRepository.findAll().stream()
                .filter(u -> refreshToken.equals(u.getRefreshToken()))
                .findFirst();
    }

    @Override
    public List<UserSummarizationHistoryResponse> userSummarizationHistory(String userId) {
        List<SummarizationEntity> summarizationEntities = summarizationService.findByUserId(userId);
        return summarizationEntities.stream()
                .map(h -> new UserSummarizationHistoryResponse(
                        h.getId(),
                        h.getCreatedAt(),
                        h.getResult().getSummary(),
                        h.getSummaryType().toString(),
                        h.getDocumentType().getExtension(),
                        h.getDocumentUpload().getOriginalFilename(),
                        h.getMetadata().getImageCount(),
                        h.getMetadata().getParagraphCount(),
                        h.getMetadata().getSlideCount(),
                        h.getMetadata().getProcessingTime(),
                        h.getMetadata().getTableCount(),
                        h.getMetadata().getWordCount(),
                        h.getDocumentUpload().getFileSize()
                ))
                .toList();
    }

    @Override
    public void deleteSummary(String summaryId) {
        summarizationService.deleteById(summaryId);
    }

    @Override
    public List<UserProfileDto> getAllUsers() {
        List<UserEntity> users = userRepository.findAll();
    return users.stream()
        .map(user -> {
            List<SummarizationEntity> history = summarizationService.findByUserId(user.getId());
            List<UserSummarizationHistoryResponse> historyDtos = history.stream()
                .map(h -> new UserSummarizationHistoryResponse(
                        h.getId(),
                        h.getCreatedAt(),
                        h.getResult().getSummary(),
                        h.getSummaryType().toString(),
                        h.getDocumentType().getExtension(),
                        h.getDocumentUpload().getOriginalFilename(),
                        h.getMetadata().getImageCount(),
                        h.getMetadata().getParagraphCount(),
                        h.getMetadata().getSlideCount(),
                        h.getMetadata().getProcessingTime(),
                        h.getMetadata().getTableCount(),
                        h.getMetadata().getWordCount(),
                        h.getDocumentUpload().getFileSize()
                ))
                .toList();
            return UserProfileDto.from(user, historyDtos);
        })
        .toList();
    }
}