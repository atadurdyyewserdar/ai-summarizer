package com.aissummarizer.jennet.common.model;

import lombok.Getter;

import java.util.Set;

@Getter
public final class HealthStatus {
    private final String status;
    private final Set<String> supportedTypes;
    private final long timestamp;

    public HealthStatus(String status, Set<String> supportedTypes) {
        this.status = status;
        this.supportedTypes = supportedTypes;
        this.timestamp = System.currentTimeMillis();
    }

}
