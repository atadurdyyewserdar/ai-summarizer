package com.aissummarizer.jennet.document.model;

import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.document.dto.ImageData;
import com.aissummarizer.jennet.document.service.DocumentContent;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container for TXT content
 */
@Data
/**
 * Container for TXT content
 */
public class TxtDocumentContent implements DocumentContent {
    private final List<String> paragraphs;

    public TxtDocumentContent(List<String> paragraphs) {
        this.paragraphs = Collections.unmodifiableList(new ArrayList<>(paragraphs));
    }

    public void addParagraph(String paragraph) {
        if (paragraph != null && !paragraph.trim().isEmpty()) {
            paragraphs.add(paragraph);
        }
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    /**
     * Get all text as a single string
     */
    public String getAllText() {
        return String.join("\n\n", paragraphs);
    }

    /**
     * Get text with paragraph numbers
     */
    public String getTextWithParagraphNumbers() {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < paragraphs.size(); i++) {
            text.append(String.format("Paragraph %d:\n%s\n\n", i + 1, paragraphs.get(i)));
        }
        return text.toString();
    }

    /**
     * Get approximate word count
     */
    public int getWordCount() {
        int count = 0;
        for (String paragraph : paragraphs) {
            if (!paragraph.trim().isEmpty()) {
                count += paragraph.trim().split("\\s+").length;
            }
        }
        return count;
    }

    @Override
    public DocumentType getType() {
        return DocumentType.TXT;
    }

    @Override
    public boolean hasImages() {
        return false;
    }

    @Override
    public List<ImageData> getImages() {
        return Collections.emptyList();
    }

    /**
     * Search for paragraphs containing a keyword
     */
    public List<String> searchParagraphs(String keyword) {
        List<String> matches = new ArrayList<>();
        for (String paragraph : paragraphs) {
            if (paragraph.toLowerCase().contains(keyword.toLowerCase())) {
                matches.add(paragraph);
            }
        }
        return matches;
    }

    /**
     * Get formatted content for OpenAI
     */
    public String getFormattedContent() {
        StringBuilder formatted = new StringBuilder();
        formatted.append("=== TEXT DOCUMENT CONTENT ===\n\n");
        formatted.append(getAllText());
        formatted.append("\n\n--- STATISTICS ---\n");
        formatted.append("Paragraphs: ").append(paragraphs.size()).append("\n");
        formatted.append("Words (approx): ").append(getWordCount()).append("\n");
        return formatted.toString();
    }
}