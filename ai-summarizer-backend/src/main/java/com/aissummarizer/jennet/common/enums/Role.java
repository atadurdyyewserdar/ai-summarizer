package com.aissummarizer.jennet.common.enums;

public enum Role {
    ROLE_ADMIN,
    ROLE_USER,
    ROLE_MODERATOR;

    public String getValue() {
        return this.name();
    }
}
