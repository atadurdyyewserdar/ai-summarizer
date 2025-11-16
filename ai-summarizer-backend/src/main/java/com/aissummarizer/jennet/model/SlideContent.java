package com.aissummarizer.jennet.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SlideContent {
    private int slideNumber;
    private List<String> textItems = new ArrayList<>();
    private List<ImageData> images = new ArrayList<>();

    public SlideContent(int slideNumber) {
        this.slideNumber = slideNumber;
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