package com.aissummarizer.jennet.common.model;

import java.util.Set;

public final class HealthStatus {
    private final String status;
    private final Set<String> supportedTypes;
    private final long timestamp;

    public HealthStatus(String status, Set<String> supportedTypes) {
        this.status = status;
        this.supportedTypes = supportedTypes;
        this.timestamp = System.currentTimeMillis();
    }

    public String getStatus() { return status; }
    public Set<String> getSupportedTypes() { return supportedTypes; }
    public long getTimestamp() { return timestamp; }
}
