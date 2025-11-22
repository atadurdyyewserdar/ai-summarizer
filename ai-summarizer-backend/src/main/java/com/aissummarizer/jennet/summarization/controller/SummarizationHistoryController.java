package com.aissummarizer.jennet.summarization.controller;

import com.aissummarizer.jennet.summarization.dto.SummarizationHistoryResponse;
import com.aissummarizer.jennet.summarization.dto.CreateSummarizationHistoryRequest;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.summarization.service.SummarizationHistoryService;
import com.aissummarizer.jennet.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST endpoints for accessing and managing summarization history.
 */
@RestController
@RequestMapping("/api/summaries")
@RequiredArgsConstructor
public class SummarizationHistoryController {

    private final SummarizationHistoryService historyService;
    private final UserService userService;

    /**
     * Returns the current user's summarization history in reverse chronological order.
     */
    @GetMapping
    public List<SummarizationHistoryResponse> getMyHistory(Authentication auth) {
        String username = auth.getName();
        UserEntity user = userService.getByUsername(username);

        return historyService.getHistoryForUser(user.getId())
                .stream()
                .map(SummarizationHistoryResponse::from)
                .toList();
    }

    /**
     * Stores a new summarization history entry for the current user.
     * <p>
     * In your final design, the summarization endpoint itself may call this
     * service method rather than the frontend calling it directly.
     */
    @PostMapping
    public SummarizationHistoryResponse addMyHistory(
            Authentication auth,
            @RequestBody CreateSummarizationHistoryRequest request
    ) {
        String username = auth.getName();
        UserEntity user = userService.getByUsername(username);

        var entity = historyService.addEntry(
                user.getId(),
                request.inputText(),
                request.summaryText()
        );

        return SummarizationHistoryResponse.from(entity);
    }
}