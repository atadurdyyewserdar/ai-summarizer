package com.aissummarizer.jennet.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import javax.validation.constraints.*;

@Configuration
@ConfigurationProperties(prefix = "ai.summarizer")
@Validated
public class AiSummarizerConfig {

    @NotBlank(message = "API key is required")
    private String apiKey;

    @NotBlank(message = "Model name is required")
    private String model = "gpt-5-mini";

    @Min(value = 100, message = "Max tokens must be at least 100")
    @Max(value = 10000, message = "Max tokens cannot exceed 10000")
    private int defaultMaxTokens = 2000;

    @DecimalMin(value = "0.0", message = "Temperature must be at least 0.0")
    @DecimalMax(value = "2.0", message = "Temperature cannot exceed 2.0")
    private double defaultTemperature = 1;

    @Min(value = 1, message = "Max file size must be positive")
    private long maxFileSizeBytes = 50_000_000; // 50MB

    @Min(value = 1000, message = "Request timeout must be at least 1 second")
    private int requestTimeoutMs = 60000; // 60 seconds

    // Getters and setters
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getDefaultMaxTokens() { return defaultMaxTokens; }
    public void setDefaultMaxTokens(int defaultMaxTokens) {
        this.defaultMaxTokens = defaultMaxTokens;
    }

    public double getDefaultTemperature() { return defaultTemperature; }
    public void setDefaultTemperature(double defaultTemperature) {
        this.defaultTemperature = defaultTemperature;
    }

    public long getMaxFileSizeBytes() { return maxFileSizeBytes; }
    public void setMaxFileSizeBytes(long maxFileSizeBytes) {
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    public int getRequestTimeoutMs() { return requestTimeoutMs; }
    public void setRequestTimeoutMs(int requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }
}