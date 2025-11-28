package com.aissummarizer.jennet.apilog;

import com.aissummarizer.jennet.apilog.dto.ApiUsageResponseDto;
import com.aissummarizer.jennet.apilog.service.ApiUsageLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usage")
@RequiredArgsConstructor
public class ApiUsageController {
    private final ApiUsageLogService apiUsageLogService;

    @GetMapping("/get-logs")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<ApiUsageResponseDto>> getAllLogs() {
        List<ApiUsageResponseDto> list = apiUsageLogService.getAllLog();
        return ResponseEntity.ok(list);
    }
}
