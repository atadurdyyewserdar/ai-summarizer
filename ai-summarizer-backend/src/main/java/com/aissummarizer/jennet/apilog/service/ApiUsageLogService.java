package com.aissummarizer.jennet.apilog.service;

import com.aissummarizer.jennet.apilog.dto.ApiUsageResponseDto;
import com.aissummarizer.jennet.apilog.entity.ApiUsageLogEntity;
import com.aissummarizer.jennet.user.entity.UserEntity;

import java.util.List;

/**
 * Service for recording usage logs of API requests.
 * <p>
 * This service is invoked whenever a REST endpoint is executed.
 */
public interface ApiUsageLogService {

    /**
     * Saves a log entry describing API usage.
     *
     * @param log the ApiUsageLogEntity to save
     */
    void log(ApiUsageLogEntity log);

    /**
     * A convenience method for logging API usage without manually building the entity.
     *
     * @param userName            userName of the user (nullable)
     * @param endpoint          the HTTP endpoint accessed
     * @param httpMethod        the HTTP method used
     * @param requestSizeBytes  number of bytes in the request payload
     * @param responseSizeBytes number of bytes in the response payload
     * @param processingTimeMs  time spent processing the request
     */
    void log(
            String userName,
            String endpoint,
            String httpMethod,
            long requestSizeBytes,
            long responseSizeBytes,
            long processingTimeMs
    );

    List<ApiUsageResponseDto> getAllLog();
}