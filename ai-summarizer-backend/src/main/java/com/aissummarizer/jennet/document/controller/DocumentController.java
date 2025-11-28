package com.aissummarizer.jennet.document.controller;

import com.aissummarizer.jennet.common.exception.AiSummarizationException;
import com.aissummarizer.jennet.common.exception.DocumentProcessingException;
import com.aissummarizer.jennet.common.exception.InvalidFileException;
import com.aissummarizer.jennet.common.exception.UnsupportedDocumentTypeException;
import com.aissummarizer.jennet.common.enums.ErrorCode;
import com.aissummarizer.jennet.common.validator.FileValidator;
import com.aissummarizer.jennet.document.entity.DocumentUploadEntity;
import com.aissummarizer.jennet.document.service.DocumentUploadService;
import com.aissummarizer.jennet.document.tools.FileUtils;
import com.aissummarizer.jennet.security.JwtService;
import com.aissummarizer.jennet.summarization.enums.SummaryType;
import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.summarization.model.SummaryOptions;
import com.aissummarizer.jennet.common.model.ApiResponse;
import com.aissummarizer.jennet.document.model.DocumentTypeInfo;
import com.aissummarizer.jennet.common.model.HealthStatus;
import com.aissummarizer.jennet.summarization.model.SummaryResult;
import com.aissummarizer.jennet.document.service.DocumentSummarizerService;
import com.aissummarizer.jennet.summarization.repository.SummarizationRepository;
import com.aissummarizer.jennet.summarization.repository.SummaryMetadataRepository;
import com.aissummarizer.jennet.summarization.repository.SummaryResultRepository;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.user.service.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/documents")
@Validated
@AllArgsConstructor
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentSummarizerService summarizerService;
    private final FileValidator fileValidator;
    private final DocumentUploadService documentUploadService;

    /**
     * Uploads a file, extracts content, summarizes it, computes metadata,
     * stores all details, and returns summary output.
     *
     * @param file uploaded document (txt, pdf, docx, pptx)
     * @param type type of summary (BRIEF, COMPREHENSIVE, KEY_POINTS, etc.)
     */
    @PostMapping(value = "/summarize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SummaryResult>> summarize(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam("userName") @NotNull String userName,
            @RequestParam(value = "type", defaultValue = "COMPREHENSIVE")
            @Valid SummaryType type,
            @RequestParam(value = "customSummary", required = false)
            String customPrompt,
            @RequestParam(value = "maxTokens", required = false)
            @Min(100) @Max(10000) Integer maxTokens
    ) throws InvalidFileException, UnsupportedDocumentTypeException {

        log.info("Received summarization request: file={}, type={}",
                file.getOriginalFilename(), type);

        DocumentUploadEntity uploadEntity = documentUploadService.uploadDocument(file, userName);

        try {
            SummaryOptions.Builder optionsBuilder = SummaryOptions.builder()
                    .type(type)
                    .customPrompt(customPrompt);

            if (maxTokens != null) {
                optionsBuilder.maxTokens(maxTokens);
            }

            SummaryOptions options = optionsBuilder.build();
            SummaryResult result = summarizerService.summarizeDocument(file, options, userName, uploadEntity);

            log.info("Successfully summarized: file={}, processingTime={}ms",
                    file.getOriginalFilename(), result.getMetadata().getProcessingTimeMs());

            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (UnsupportedDocumentTypeException e) {
            log.warn("Unsupported file type: {}", e.getFileType());
            return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(ApiResponse.error(
                            ErrorCode.UNSUPPORTED_FILE_TYPE,
                            e.getMessage()
                    ));

        } catch (InvalidFileException e) {
            log.warn("Invalid file: {}", e.getMessage());
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(
                            ErrorCode.INVALID_FILE,
                            e.getMessage()
                    ));

        } catch (AiSummarizationException e) {
            log.error("AI summarization failed", e);
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.error(
                            ErrorCode.AI_SERVICE_ERROR,
                            "AI service temporarily unavailable"
                    ));

        } catch (DocumentProcessingException e) {
            log.error("Document processing failed: {}", file.getOriginalFilename(), e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(
                            ErrorCode.PROCESSING_ERROR,
                            "Failed to process document"
                    ));
        }
    }

    @PostMapping(value = "/summarize/custom-text")
    public ResponseEntity<ApiResponse<SummaryResult>> summarizeTextWithoutFile(
            @RequestParam("userName") String userName,
            @RequestParam(value = "type", defaultValue = "COMPREHENSIVE")
            @Valid SummaryType type,
            @RequestParam(value = "customSummary", required = false)
            String customPrompt,
            @RequestParam(value = "customText", required = false)
            String customText
            ) throws DocumentProcessingException {

            log.info("Received custom summarization request");

            SummaryOptions.Builder optionsBuilder = SummaryOptions.builder()
                    .type(type)
                    .customPrompt(customPrompt);

            DocumentUploadEntity documentUploadEntity = documentUploadService.uploadMockDocument(customText, userName);

            SummaryOptions options = optionsBuilder.build();

            SummaryResult result = summarizerService.summarizeDocument(customText, options, userName, documentUploadEntity);

            return ResponseEntity.ok(ApiResponse.success(result));

    }

    @GetMapping("/supported-types")
    public ResponseEntity<ApiResponse<List<DocumentTypeInfo>>> getSupportedTypes() {
        List<DocumentTypeInfo> types = summarizerService.getSupportedFileTypes().stream()
                .map(ext -> {
                    try {
                        return new DocumentTypeInfo(
                                ext,
                                DocumentType.fromExtension(ext).getDescription()
                        );
                    } catch (UnsupportedDocumentTypeException e) {
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(DocumentTypeInfo::getExtension))
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(types));
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<HealthStatus>> health() {
        HealthStatus status = new HealthStatus(
                "UP",
                summarizerService.getSupportedFileTypes()
        );
        return ResponseEntity.ok(ApiResponse.success(status));
    }

    private ResponseEntity<ApiResponse<SummaryResult>> handleProcessingException(
            DocumentProcessingException e, String filename) {

        if (e instanceof UnsupportedDocumentTypeException) {
            return ResponseEntity
                    .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                    .body(ApiResponse.error(ErrorCode.UNSUPPORTED_FILE_TYPE, e.getMessage()));
        }

        if (e instanceof InvalidFileException) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.error(ErrorCode.INVALID_FILE, e.getMessage()));
        }

        log.error("Unexpected processing error: {}", filename, e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(ErrorCode.PROCESSING_ERROR, "Failed to process document"));
    }
}