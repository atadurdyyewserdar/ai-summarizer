package com.aissummarizer.jennet.document.extractor;

import com.aissummarizer.jennet.document.model.TxtDocumentContent;
import com.aissummarizer.jennet.document.service.DocumentContent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MockTxtExtractor {
    public static DocumentContent extract(String customText) {
        List<String> paragraphs = extractParagraphsFromText(customText);
        return new TxtDocumentContent(paragraphs);
    }

    private static List<String> extractParagraphsFromText(String customText) {
        if (customText == null || customText.isBlank()) {
            return new ArrayList<>();
        }

        // The regex "\\R\\s*\\R" matches:
        // \\R   -> Any line break sequence (e.g., \n, \r\n, \r)
        // \\s* -> Zero or more whitespace characters (handles lines containing only spaces)
        // \\R   -> Another line break sequence
        // The "+" ensures we handle multiple blank lines between paragraphs gracefully.
        String[] rawParagraphs = customText.strip().split("(\\R\\s*){2,}");

        // Convert array to List, filtering out any empty results just in case
        return Arrays.stream(rawParagraphs)
                .map(String::strip)       // Remove leading/trailing whitespace from each paragraph
                .filter(p -> !p.isEmpty()) // Ensure no empty strings sneak in
                .toList();
    }
}
