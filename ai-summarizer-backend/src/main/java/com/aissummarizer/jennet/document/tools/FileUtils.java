package com.aissummarizer.jennet.document.tools;

import org.apache.commons.io.FilenameUtils;

/**
 * File utility methods
 */
public final class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Get file extension from filename
     * @param filename Filename with extension
     * @return Extension without dot
     * @throws IllegalArgumentException if invalid filename
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
        String extension = FilenameUtils.getExtension(filename);
        if (extension.isEmpty()) {
            throw new IllegalArgumentException("Filename has no extension: " + filename);
        }
        return extension;
    }

    /**
     * Format file size in human-readable format
     * @param bytes File size in bytes
     * @return Formatted string (e.g., "1.5 MB")
     */
    public static String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}
