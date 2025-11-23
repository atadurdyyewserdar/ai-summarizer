package com.aissummarizer.jennet.document.factory;

import com.aissummarizer.jennet.common.exception.UnsupportedDocumentTypeException;
import com.aissummarizer.jennet.document.service.DocumentExtractor;
import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.document.tools.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Factory to create appropriate document extractors
 * Thread-safe, immutable after construction
 */
@Component
public class DocumentExtractorFactory {

    private static final Logger logger = LoggerFactory.getLogger(DocumentExtractorFactory.class);

    private final Map<DocumentType, DocumentExtractor<?>> extractors;

    @Autowired
    public DocumentExtractorFactory(List<DocumentExtractor<?>> extractorList) {
        this.extractors = Collections.unmodifiableMap(
                extractorList.stream()
                        .collect(Collectors.toMap(
                                DocumentExtractor::getDocumentType,
                                Function.identity()
                        ))
        );

        logger.info("Initialized factory with {} extractors: {}",
                extractors.size(), extractors.keySet());
    }

    /**
     * Get extractor for given filename
     * @param filename File name with extension
     * @return Appropriate extractor
     * @throws UnsupportedDocumentTypeException if type not supported
     */
    public DocumentExtractor<?> getExtractor(String filename)
            throws UnsupportedDocumentTypeException {

        String extension = FileUtils.getFileExtension(filename);
        logger.info(("extension is: " + extension));
        DocumentType type = DocumentType.fromExtension(extension);
        logger.info("DocumentType is: " + type);
        DocumentExtractor<?> extractor = extractors.get(type);
        if (extractor == null) {
            throw new UnsupportedDocumentTypeException(extension);
        }

        return extractor;
    }

    /**
     * Get all supported file extensions
     * @return Unmodifiable set of extensions
     */
    public Set<String> getSupportedExtensions() {
        return extractors.keySet().stream()
                .map(DocumentType::getExtension)
                .collect(Collectors.toUnmodifiableSet());
    }
}