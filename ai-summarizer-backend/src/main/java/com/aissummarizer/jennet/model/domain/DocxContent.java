package com.aissummarizer.jennet.model.domain;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Data
@Service
public class DocxContent {
    private List<String> paragraphs = new ArrayList<>();
    private List<TableData> tables = new ArrayList<>();
    private List<DocxImageData> images = new ArrayList<>();

    public void addParagraph(String paragraph) {
        paragraphs.add(paragraph);
    }

    public void addTable(TableData table) {
        tables.add(table);
    }

    public void addImage(DocxImageData image) {
        images.add(image);
    }

    public List<String> getParagraphs() {
        return paragraphs;
    }

    public List<TableData> getTables() {
        return tables;
    }

    public List<DocxImageData> getImages() {
        return images;
    }

    /**
     * Check if image already exists (by base64 data)
     */
    public boolean hasImage(String base64Data) {
        for (DocxImageData img : images) {
            if (img.getBase64Data().equals(base64Data)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all text concatenated
     */
    public String getAllText() {
        StringBuilder allText = new StringBuilder();

        // Add paragraphs
        for (String paragraph : paragraphs) {
            allText.append(paragraph).append("\n");
        }

        // Add tables as text
        if (!tables.isEmpty()) {
            allText.append("\n=== TABLES ===\n");
            for (TableData table : tables) {
                allText.append(table.toText()).append("\n");
            }
        }

        return allText.toString();
    }

    /**
     * Get formatted content with structure
     */
    public String getFormattedContent() {
        StringBuilder formatted = new StringBuilder();

        formatted.append("=== DOCUMENT CONTENT ===\n\n");

        // Add paragraphs
        if (!paragraphs.isEmpty()) {
            formatted.append("--- TEXT ---\n");
            for (String paragraph : paragraphs) {
                formatted.append(paragraph).append("\n\n");
            }
        }

        // Add tables
        if (!tables.isEmpty()) {
            formatted.append("--- TABLES ---\n");
            for (TableData table : tables) {
                formatted.append("Table ").append(table.getTableNumber()).append(":\n");
                formatted.append(table.toText()).append("\n\n");
            }
        }

        // Add image count
        if (!images.isEmpty()) {
            formatted.append("--- IMAGES ---\n");
            formatted.append("This document contains ").append(images.size())
                    .append(" image(s)\n");
        }

        return formatted.toString();
    }
}