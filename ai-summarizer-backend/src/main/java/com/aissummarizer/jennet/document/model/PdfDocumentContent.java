package com.aissummarizer.jennet.document.model;

import com.aissummarizer.jennet.document.service.DocumentContent;
import com.aissummarizer.jennet.document.dto.ImageData;
import com.aissummarizer.jennet.document.enums.DocumentType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class PdfDocumentContent implements DocumentContent {

    private final List<String> textPages;
    private final List<ImageData> images;
    private final int totalPages;

    public PdfDocumentContent(List<String> textPages, List<ImageData> images, int totalPages) {
        this.textPages = Collections.unmodifiableList(new ArrayList<>(textPages));
        this.images = Collections.unmodifiableList(new ArrayList<>(images));
        this.totalPages = totalPages;
    }

    public List<String> getTextPages() {
        return textPages;
    }

    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public List<ImageData> getImages() {
        return images;
    }

    @Override
    public String getAllText() {
        StringBuilder text = new StringBuilder();

        for (int i = 0; i < textPages.size(); i++) {
            text.append("=== PAGE ").append(i + 1).append(" ===\n");
            text.append(textPages.get(i)).append("\n\n");
        }

        return text.toString();
    }

    @Override
    public int getWordCount() {
        int count = 0;
        for (String page : textPages) {
            if (page != null && !page.trim().isEmpty()) {
                count += page.split("\\s+").length;
            }
        }
        return count;
    }

    @Override
    public DocumentType getType() {
        return DocumentType.PDF;
    }

    @Override
    public boolean hasImages() {
        return !images.isEmpty();
    }
}