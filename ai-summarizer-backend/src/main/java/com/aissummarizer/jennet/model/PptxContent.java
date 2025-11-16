package com.aissummarizer.jennet.model;

import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Container for all PPTX content
 */
@Service
@Data
public class PptxContent {
    private List<SlideContent> slides = new ArrayList<>();

    public void addSlide(SlideContent slide) {
        slides.add(slide);
    }

    /**
     * Get all images from all slides
     */
    public List<ImageData> getAllImages() {
        List<ImageData> allImages = new ArrayList<>();
        for (SlideContent slide : slides) {
            allImages.addAll(slide.getImages());
        }
        return allImages;
    }

    /**
     * Get all text from all slides concatenated
     */
    public String getAllText() {
        StringBuilder allText = new StringBuilder();
        for (SlideContent slide : slides) {
            allText.append("=== Slide ").append(slide.getSlideNumber()).append(" ===\n");
            for (String text : slide.getTextItems()) {
                allText.append(text).append("\n");
            }
            allText.append("\n");
        }
        return allText.toString();
    }

    /**
     * Get total count of images across all slides
     */
    public int getTotalImageCount() {
        int count = 0;
        for (SlideContent slide : slides) {
            count += slide.getImages().size();
        }
        return count;
    }
}