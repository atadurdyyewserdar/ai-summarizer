package com.aissummarizer.jennet.document.model;

import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.document.dto.ImageData;
import com.aissummarizer.jennet.document.service.DocumentContent;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Container for all PPTX content
 */
@Service
@Data
public final class PptxDocumentContent implements DocumentContent {
    private final List<SlideContent> slides;

    public PptxDocumentContent(List<SlideContent> slides) {
        this.slides = Collections.unmodifiableList(new ArrayList<>(slides));
    }

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

    @Override
    public int getWordCount() {
        int count = 0;
        for (SlideContent slide : slides) {
            for (String text : slide.getTextItems()) {
                count += text.split("\\s+").length;
            }
        }
        return count;
    }

    @Override
    public DocumentType getType() {
        return DocumentType.PPTX;
    }

    @Override
    public boolean hasImages() {
        return !getAllImages().isEmpty();
    }

    @Override
    public List<ImageData> getImages() {
        return getAllImages();
    }

}