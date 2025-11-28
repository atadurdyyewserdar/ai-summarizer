package com.aissummarizer.jennet.document.entity;

import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents an uploaded document (optional for text-based summarization).
 */
@Entity
@Table(name = "document_uploads")
@Getter @Setter
@AllArgsConstructor @Builder
@NoArgsConstructor
public class DocumentUploadEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private String id;

    @PrePersist
    public void ensureId() {
        if (id == null) id = UUID.randomUUID().toString();
    }

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentType documentType;

    @CreationTimestamp
    @Column(nullable = false)
    @JsonFormat(pattern = "MMM dd, yyyy 'on' HH:mm", locale = "en")
    private LocalDateTime uploadedAt;

    /** Original filename uploaded by the user. */
    @Column(name = "original_filename", nullable = false)
    private String originalFilename;

    /** File extension such as txt, pdf, docx, pptx. */
    @Column(name = "file_extension", nullable = false, length = 10)
    private String fileExtension;

    /** MIME type (optional but useful for consistency). */
    @Column(name = "mime_type")
    private String mimeType;

    /** File size in bytes. */
    @Column(name = "file_size")
    private long fileSize;

    /**
     * Whether extraction was successful or not.
     * Helps UI show status when listing uploaded files.
     */
    @Column(name = "extraction_success")
    private boolean extractionSuccess;

    /**
     * Optional field: if extraction fails, record reason.
     */
    @Column(name = "extraction_error_message")
    private String extractionErrorMessage;

}