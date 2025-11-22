package com.aissummarizer.jennet.document.controller;

import com.aissummarizer.jennet.common.exception.AiSummarizationException;
import com.aissummarizer.jennet.common.exception.DocumentProcessingException;
import com.aissummarizer.jennet.common.exception.InvalidFileException;
import com.aissummarizer.jennet.common.exception.UnsupportedDocumentTypeException;
import com.aissummarizer.jennet.common.enums.ErrorCode;
import com.aissummarizer.jennet.summarization.enums.SummaryType;
import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.summarization.model.SummaryOptions;
import com.aissummarizer.jennet.common.model.ApiResponse;
import com.aissummarizer.jennet.document.model.DocumentTypeInfo;
import com.aissummarizer.jennet.common.model.HealthStatus;
import com.aissummarizer.jennet.summarization.model.SummaryResult;
import com.aissummarizer.jennet.document.service.DocumentSummarizerService;
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
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/documents")
@Validated
public class DocumentController {

    private static final Logger log = LoggerFactory.getLogger(DocumentController.class);

    private final DocumentSummarizerService summarizerService;

    @Autowired
    public DocumentController(DocumentSummarizerService summarizerService) {
        this.summarizerService = Objects.requireNonNull(summarizerService);
    }

    @PostMapping(value = "/summarize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SummaryResult>> summarize(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam(value = "type", defaultValue = "COMPREHENSIVE")
            @Valid SummaryType type,
            @RequestParam(value = "maxTokens", required = false)
            @Min(100) @Max(10000) Integer maxTokens,
            @RequestParam(value = "temperature", required = false)
            @DecimalMin("0.0") @DecimalMax("2.0") Double temperature) {

        log.info("Received summarization request: file={}, type={}",
                file.getOriginalFilename(), type);

        try {
            SummaryOptions.Builder optionsBuilder = SummaryOptions.builder()
                    .type(type);

            if (maxTokens != null) {
                optionsBuilder.maxTokens(maxTokens);
            }

            if (temperature != null) {
                optionsBuilder.temperature(temperature);
            }

            SummaryOptions options = optionsBuilder.build();
            SummaryResult result = summarizerService.summarizeDocument(file, options);

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

    @PostMapping(value = "/summarize/custom", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<SummaryResult>> summarizeWithCustomPrompt(
            @RequestParam("file") @NotNull MultipartFile file,
            @RequestParam("prompt") @NotBlank @Size(min = 10, max = 1000) String customPrompt) {

        log.info("Received custom summarization request: file={}", file.getOriginalFilename());

        try {
            SummaryOptions options = SummaryOptions.builder()
                    .customPrompt(customPrompt)
                    .build();

            SummaryResult result = summarizerService.summarizeDocument(file, options);
            return ResponseEntity.ok(ApiResponse.success(result));

        } catch (DocumentProcessingException e) {
            return handleProcessingException(e, file.getOriginalFilename());
        }
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