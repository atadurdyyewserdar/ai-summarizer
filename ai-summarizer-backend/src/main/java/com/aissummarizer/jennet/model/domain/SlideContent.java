package com.aissummarizer.jennet.model.domain;

import lombok.Data;
import java.util.List;

@Data
public class SlideContent {
    private final int slideNumber;
    private final List<String> textItems;
    private final List<ImageData> images;

    public SlideContent(int slideNumber, List<String> textItems, List<ImageData> images) {
        this.slideNumber = slideNumber;
        this.textItems = List.copyOf(textItems);
        this.images = List.copyOf(images);
    }

    public void addText(String text) {
        textItems.add(text);
    }

    public void addImage(ImageData image) {
        images.add(image);
    }

    public int getSlideNumber() {
        return slideNumber;
    }

    public List<String> getTextItems() {
        return textItems;
    }

    public List<ImageData> getImages() {
        return images;
    }

    /**
     * Get all text from this slide concatenated
     */
    public String getAllText() {
        return String.join("\n", textItems);
    }
}