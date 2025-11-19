package com.aissummarizer.jennet.tools;


import com.aissummarizer.jennet.model.domain.TxtDocumentContent;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

/**
 * Extracts content from TXT files
 */
public class TxtDocumentExtractor {

    /**
     * Extract all content from a MultipartFile (Spring Boot file upload)
     *
     * @param multipartFile The uploaded TXT file
     * @return TxtContent containing all text
     * @throws Exception if file cannot be read
     */
    public static TxtDocumentContent extractContent(MultipartFile multipartFile) throws Exception {
        try (InputStream is = multipartFile.getInputStream();
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(is, StandardCharsets.UTF_8))) {
            return extractFromReader(reader);
        }
    }

    /**
     * Extract all content from a File
     *
     * @param txtFile The TXT file to process
     * @return TxtContent containing all text
     * @throws Exception if file cannot be read
     */
    public static TxtDocumentContent extractContent(File txtFile) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(txtFile, StandardCharsets.UTF_8))) {
            return extractFromReader(reader);
        }
    }

    /**
     * Extract all content from an InputStream
     *
     * @param inputStream The input stream containing TXT data
     * @return TxtContent containing all text
     * @throws Exception if file cannot be read
     */
    public static TxtDocumentContent extractContent(InputStream inputStream) throws Exception {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return extractFromReader(reader);
        }
    }

    /**
     * Extract content from a string
     *
     * @param textContent The text content
     * @return TxtContent containing the text
     */
    public static TxtDocumentContent extractContent(String textContent) {
        TxtDocumentContent content = new TxtDocumentContent(new ArrayList<>());
        String[] paragraphs = textContent.split("\\n\\s*\\n"); // Split by empty lines

        for (String paragraph : paragraphs) {
            // Replace single line breaks with spaces within paragraph
            String cleanedParagraph = paragraph.replaceAll("\\s*\\n\\s*", " ").trim();
            if (!cleanedParagraph.isEmpty()) {
                content.addParagraph(cleanedParagraph);
            }
        }

        return content;
    }

    /**
     * Core extraction logic from BufferedReader
     */
    private static TxtDocumentContent extractFromReader(BufferedReader reader) throws IOException {
        TxtDocumentContent content = new TxtDocumentContent(new ArrayList<>());
        StringBuilder currentParagraph = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            // Empty line indicates paragraph break
            if (line.trim().isEmpty()) {
                if (currentParagraph.length() > 0) {
                    content.addParagraph(currentParagraph.toString().trim());
                    currentParagraph = new StringBuilder();
                }
            } else {
                // Add space between lines within same paragraph
                if (currentParagraph.length() > 0) {
                    currentParagraph.append(" ");
                }
                currentParagraph.append(line.trim());
            }
        }

        // Add last paragraph if exists
        if (currentParagraph.length() > 0) {
            content.addParagraph(currentParagraph.toString().trim());
        }

        return content;
    }
}
