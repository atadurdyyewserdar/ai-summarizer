package com.aissummarizer.jennet.model.response;

public final class DocumentTypeInfo {
    private final String extension;
    private final String description;

    public DocumentTypeInfo(String extension, String description) {
        this.extension = extension;
        this.description = description;
    }

    public String getExtension() { return extension; }
    public String getDescription() { return description; }
}
