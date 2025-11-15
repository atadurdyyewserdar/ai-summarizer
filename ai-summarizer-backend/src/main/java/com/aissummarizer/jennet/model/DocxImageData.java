package com.aissummarizer.jennet.model;

import lombok.Data;

/**
 * Data for a single image (reusing from PPTX extractor)
 */
@Data
public class DocxImageData {
    private String base64Data;
    private String format;
    private int sizeBytes;

    public DocxImageData(String base64Data, String format, int sizeBytes) {
        this.base64Data = base64Data;
        this.format = format;
        this.sizeBytes = sizeBytes;
    }

    public String getBase64Data() {
        return base64Data;
    }

    public String getFormat() {
        return format;
    }

    public int getSizeBytes() {
        return sizeBytes;
    }

    /**
     * Get the data URL for use in OpenAI API
     */
    public String getDataUrl() {
        return "data:image/" + format + ";base64," + base64Data;
    }
}