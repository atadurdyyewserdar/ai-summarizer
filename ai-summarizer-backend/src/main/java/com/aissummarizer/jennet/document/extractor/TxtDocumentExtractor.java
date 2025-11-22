package com.aissummarizer.jennet.document.extractor;

import com.aissummarizer.jennet.common.exception.DocumentProcessingException;
import com.aissummarizer.jennet.document.service.DocumentExtractor;
import com.aissummarizer.jennet.document.model.TxtDocumentContent;
import com.aissummarizer.jennet.document.enums.DocumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class TxtDocumentExtractor implements DocumentExtractor<TxtDocumentContent> {

    private static final Logger logger = LoggerFactory.getLogger(TxtDocumentExtractor.class);

    @Override
    public TxtDocumentContent extract(InputStream inputStream)
            throws DocumentProcessingException {

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            List<String> paragraphs = extractParagraphs(reader);
            logger.info("Extracted {} paragraphs from TXT", paragraphs.size());

            return new TxtDocumentContent(paragraphs);

        } catch (IOException e) {
            throw new DocumentProcessingException("Failed to extract TXT content", e);
        }
    }

    @Override
    public boolean supports(String fileExtension) {
        return "txt".equalsIgnoreCase(fileExtension);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.TXT;
    }

    private List<String> extractParagraphs(BufferedReader reader) throws IOException {
        List<String> paragraphs = new ArrayList<>();
        StringBuilder currentParagraph = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.trim().isEmpty()) {
                addParagraphIfNotEmpty(paragraphs, currentParagraph);
                currentParagraph = new StringBuilder();
            } else {
                if (currentParagraph.length() > 0) {
                    currentParagraph.append(" ");
                }
                currentParagraph.append(line.trim());
            }
        }

        addParagraphIfNotEmpty(paragraphs, currentParagraph);
        return paragraphs;
    }

    private void addParagraphIfNotEmpty(List<String> paragraphs, StringBuilder paragraph) {
        String text = paragraph.toString().trim();
        if (!text.isEmpty()) {
            paragraphs.add(text);
        }
    }
}
