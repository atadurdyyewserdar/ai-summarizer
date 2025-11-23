package com.aissummarizer.jennet.apilog.filter;

import com.aissummarizer.jennet.apilog.service.ApiUsageLogService;
import com.aissummarizer.jennet.apilog.tool.RequestSizeWrapper;
import com.aissummarizer.jennet.apilog.tool.ResponseSizeWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A global request logging filter that records all API requests into the ApiUsageLog table.
 * <p>
 * This runs once per request, after authentication, and includes:
 * - user ID (if logged in)
 * - request URL & method
 * - timestamp
 * - total execution duration
 * - response status code
 */
@Component
public class ApiUsageLoggingFilter extends OncePerRequestFilter {

    private final ApiUsageLogService apiUsageLogService;

    public ApiUsageLoggingFilter(ApiUsageLogService apiUsageLogService) {
        this.apiUsageLogService = apiUsageLogService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = System.currentTimeMillis() - start;

            // Extract authenticated userName if present
            String userName = null;
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof org.springframework.security.core.userdetails.User user) {
                    userName = user.getUsername();
                }
            }

            RequestSizeWrapper requestSizeWrapper = new RequestSizeWrapper(request);
            ResponseSizeWrapper responseSizeWrapper = new ResponseSizeWrapper(response);

            // Persist the log
            apiUsageLogService.log(
                    userName,
                    request.getRequestURI(),
                    request.getMethod(),
                    requestSizeWrapper.getRequestSize(),
                    responseSizeWrapper.getResponseSize(),
                    durationMs
            );
        }
    }
}
