package com.aissummarizer.jennet.document.dto;

import lombok.Data;

/**
 * Data for a single image (reusing from PPTX extractor)
 */
@Data
public class ImageData {
    private String base64Data;
    private String format;
    private int sizeBytes;

    public ImageData(String base64Data, String format, int sizeBytes) {
        this.base64Data = base64Data;
        this.format = format;
        this.sizeBytes = sizeBytes;
    }

    /**
     * Get the data URL for use in OpenAI API
     */
    public String getDataUrl() {
        return "data:image/" + format + ";base64," + base64Data;
    }
}