package com.aissummarizer.jennet.summarization.enums;

import java.util.Objects;

public enum SummaryType {
    COMPREHENSIVE("Comprehensive analysis with details"),
    BRIEF("Quick 2-3 sentence summary"),
    KEY_POINTS("Bullet-point key information"),
    EXECUTIVE("Professional executive summary"),
    SENTIMENT("Sentiment and tone analysis"),
    TECHNICAL("Technical documentation summary");

    private final String description;

    SummaryType(String description) {
        this.description = Objects.requireNonNull(description);
    }

    public String getDescription() {
        return description;
    }
}
