package com.aissummarizer.jennet.apilog.service;

import com.aissummarizer.jennet.apilog.entity.ApiUsageLogEntity;
import com.aissummarizer.jennet.apilog.repository.ApiUsageLogRepository;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.user.repository.UserRepository;
import com.aissummarizer.jennet.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of the API usage logging service.
 * <p>
 * Stores metadata of every request into the database.
 */
@Service
@AllArgsConstructor
public class ApiUsageLogServiceImpl implements ApiUsageLogService {

    private static final Logger logger = LoggerFactory.getLogger(ApiUsageLogServiceImpl.class);
    private final UserService userService;
    private final ApiUsageLogRepository apiUsageLogRepository;

    @Override
    public void log(ApiUsageLogEntity log) {

        // Ensure timestamp is always provided
        if (log.getCreatedAt() == null) {
            log.setCreatedAt(LocalDateTime.now());
        }
        apiUsageLogRepository.save(log);
        logger.debug("API usage log saved for endpoint: {}", log.getEndpoint());
    }

    @Override
    public void log(
            String userName,
            String endpoint,
            String httpMethod,
            long requestSizeBytes,
            long responseSizeBytes,
            long processingTimeMs
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserEntity user = userService.getByUsername(authentication.getName());
        ApiUsageLogEntity log = ApiUsageLogEntity.builder()
                .user(user)
                .endpoint(endpoint)
                .httpMethod(httpMethod)
                .requestSizeBytes(requestSizeBytes)
                .responseSizeBytes(responseSizeBytes)
                .processingTimeMs(processingTimeMs)
                .createdAt(LocalDateTime.now())
                .build();

        // Delegate to main log() method
        apiUsageLogRepository.save(log);
        logger.debug("API log created via helper method for endpoint: {}", endpoint);
    }
}