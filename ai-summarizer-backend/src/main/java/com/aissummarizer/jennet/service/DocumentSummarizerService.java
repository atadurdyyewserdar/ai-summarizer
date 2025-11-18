package com.aissummarizer.jennet.service;

import com.aissummarizer.jennet.exceptions.DocumentProcessingException;
import com.aissummarizer.jennet.extractor.DocumentExtractor;
import com.aissummarizer.jennet.factory.DocumentExtractorFactory;
import com.aissummarizer.jennet.model.domain.DocumentContent;
import com.aissummarizer.jennet.model.request.SummaryOptions;
import com.aissummarizer.jennet.model.response.SummaryResult;
import com.aissummarizer.jennet.tools.FileUtils;
import com.aissummarizer.jennet.tools.FileValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

/**
 * Main service for document summarization
 * Handles orchestration, validation, and error handling
 */
@Service
public class DocumentSummarizerService {

    private static final Logger logger = LoggerFactory.getLogger(DocumentSummarizerService.class);
    private static final long MAX_FILE_SIZE = 50_000_000; // 50MB

    private final DocumentExtractorFactory extractorFactory;
    private final AiSummarizer aiSummarizer;
    private final FileValidator fileValidator;

    @Autowired
    public DocumentSummarizerService(
            DocumentExtractorFactory extractorFactory,
            AiSummarizer aiSummarizer,
            FileValidator fileValidator) {
        this.extractorFactory = Objects.requireNonNull(extractorFactory);
        this.aiSummarizer = Objects.requireNonNull(aiSummarizer);
        this.fileValidator = Objects.requireNonNull(fileValidator);
    }

    /**
     * Summarize uploaded document
     * @param file Uploaded file
     * @param options Summarization options
     * @return Summary result
     * @throws DocumentProcessingException if processing fails
     */
    @Transactional(readOnly = true)
    public SummaryResult summarizeDocument(
            MultipartFile file,
            SummaryOptions options) throws DocumentProcessingException {

        Objects.requireNonNull(file, "file cannot be null");
        Objects.requireNonNull(options, "options cannot be null");

        long startTime = System.currentTimeMillis();

        try {
            // 1. Validate file
            fileValidator.validate(file);

            // 2. Get appropriate extractor
            String filename = file.getOriginalFilename();
            DocumentExtractor<?> extractor = extractorFactory.getExtractor(filename);

            logger.info("Processing file: {} ({}), type: {}",
                    filename, FileUtils.formatFileSize(file.getSize()),
                    extractor.getDocumentType());

            // 3. Extract content
            DocumentContent content = extractWithLogging(extractor, file);

            // 4. Summarize with AI
            SummaryResult result = aiSummarizer.summarize(content, options);

            long duration = System.currentTimeMillis() - startTime;
            logger.info("Successfully processed {} in {}ms", filename, duration);

            return result;

        } catch (IOException e) {
            logger.error("IO error processing file: {}", file.getOriginalFilename(), e);
            throw new DocumentProcessingException("Failed to read file", e);
        } catch (Exception e) {
            logger.error("Unexpected error processing file: {}", file.getOriginalFilename(), e);
            throw new DocumentProcessingException("Unexpected error: " + e.getMessage(), e);
        }
    }

    private DocumentContent extractWithLogging(
            DocumentExtractor<?> extractor,
            MultipartFile file) throws DocumentProcessingException, IOException {

        logger.debug("Extracting content using {}", extractor.getClass().getSimpleName());
        long extractStart = System.currentTimeMillis();

        DocumentContent content = extractor.extract(file.getInputStream());

        long extractDuration = System.currentTimeMillis() - extractStart;
        logger.debug("Extraction completed in {}ms, words: {}, images: {}",
                extractDuration, content.getWordCount(), content.getImages().size());

        return content;
    }

    /**
     * Get supported file types
     * @return Set of supported extensions
     */
    public Set<String> getSupportedFileTypes() {
        return extractorFactory.getSupportedExtensions();
    }
}