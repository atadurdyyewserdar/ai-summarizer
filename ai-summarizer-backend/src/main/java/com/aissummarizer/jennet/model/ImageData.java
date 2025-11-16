package com.aissummarizer.jennet.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Data for a single image
 */
@Data
public class ImageData {
    private String base64Data;
    private String format;
    private int slideNumber;
    private int sizeBytes;

    public ImageData(String base64Data, String format, int slideNumber, int sizeBytes) {
        this.base64Data = base64Data;
        this.format = format;
        this.slideNumber = slideNumber;
        this.sizeBytes = sizeBytes;
    }

    /**
     * Get the data URL for use in OpenAI API
     */
    public String getDataUrl() {
        return "data:image/" + format + ";base64," + base64Data;
    }
}