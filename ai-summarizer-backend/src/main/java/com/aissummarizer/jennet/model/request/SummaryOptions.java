package com.aissummarizer.jennet.model.request;

import com.aissummarizer.jennet.model.enums.SummaryType;

import java.util.Objects;

/**
 * Immutable configuration for summarization
 */
public final class SummaryOptions {
    private final SummaryType type;
    private final int maxTokens;
    private final double temperature;
    private final String customPrompt;

    private SummaryOptions(Builder builder) {
        this.type = builder.type;
        this.maxTokens = builder.maxTokens;
        this.temperature = builder.temperature;
        this.customPrompt = builder.customPrompt;

        validate();
    }

    private void validate() {
        if (maxTokens <= 0 || maxTokens > 10000) {
            throw new IllegalArgumentException(
                    "maxTokens must be between 1 and 10000, got: " + maxTokens);
        }

        if (temperature < 0.0 || temperature > 2.0) {
            throw new IllegalArgumentException(
                    "temperature must be between 0.0 and 2.0, got: " + temperature);
        }
    }

    // Getters
    public SummaryType getType() { return type; }
    public int getMaxTokens() { return maxTokens; }
    public double getTemperature() { return temperature; }
    public String getCustomPrompt() { return customPrompt; }

    // Builder pattern
    public static final class Builder {
        private SummaryType type = SummaryType.COMPREHENSIVE;
        private int maxTokens = 2000;
        private double temperature = 0.7;
        private String customPrompt = null;

        public Builder type(SummaryType type) {
            this.type = Objects.requireNonNull(type, "type cannot be null");
            return this;
        }

        public Builder maxTokens(int maxTokens) {
            this.maxTokens = maxTokens;
            return this;
        }

        public Builder temperature(double temperature) {
            this.temperature = temperature;
            return this;
        }

        public Builder customPrompt(String customPrompt) {
            this.customPrompt = customPrompt;
            return this;
        }

        public SummaryOptions build() {
            return new SummaryOptions(this);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    // Default instance
    public static SummaryOptions defaultOptions() {
        return builder().build();
    }
}