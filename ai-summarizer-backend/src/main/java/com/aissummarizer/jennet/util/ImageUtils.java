package com.aissummarizer.jennet.util;

/**
 * Image utility methods
 */
public final class ImageUtils {

    private ImageUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Get image format from content type
     * @param contentType MIME content type
     * @return Image format (png, jpeg, etc.)
     */
    public static String getFormatFromContentType(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return "png"; // default
        }

        String lower = contentType.toLowerCase();
        if (lower.contains("png")) return "png";
        if (lower.contains("jpeg") || lower.contains("jpg")) return "jpeg";
        if (lower.contains("gif")) return "gif";
        if (lower.contains("webp")) return "webp";
        if (lower.contains("bmp")) return "bmp";

        return "png"; // default
    }
}
