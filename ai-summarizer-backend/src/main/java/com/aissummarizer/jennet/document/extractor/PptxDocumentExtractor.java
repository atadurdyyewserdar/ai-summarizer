package com.aissummarizer.jennet.document.extractor;

import com.aissummarizer.jennet.common.exception.DocumentProcessingException;
import com.aissummarizer.jennet.document.service.DocumentExtractor;
import com.aissummarizer.jennet.document.dto.ImageData;
import com.aissummarizer.jennet.document.model.PptxDocumentContent;
import com.aissummarizer.jennet.document.model.SlideContent;
import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.document.tools.ImageUtils;
import org.apache.poi.xslf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class PptxDocumentExtractor implements DocumentExtractor<PptxDocumentContent> {

    private static final Logger logger = LoggerFactory.getLogger(PptxDocumentExtractor.class);

    @Override
    public PptxDocumentContent extract(InputStream inputStream)
            throws DocumentProcessingException {

        try (XMLSlideShow ppt = new XMLSlideShow(inputStream)) {
            return extractFromSlideShow(ppt);
        } catch (IOException e) {
            throw new DocumentProcessingException("Failed to extract PPTX content", e);
        }
    }

    @Override
    public boolean supports(String fileExtension) {
        return "pptx".equalsIgnoreCase(fileExtension);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.PPTX;
    }

    private PptxDocumentContent extractFromSlideShow(XMLSlideShow ppt) {
        List<SlideContent> slides = new ArrayList<>();

        for (int i = 0; i < ppt.getSlides().size(); i++) {
            XSLFSlide slide = ppt.getSlides().get(i);
            slides.add(extractSlide(slide, i + 1));
        }

        logger.info("Extracted {} slides from PPTX", slides.size());
        return new PptxDocumentContent(slides);
    }

    private SlideContent extractSlide(XSLFSlide slide, int slideNumber) {
        List<String> texts = new ArrayList<>();
        List<ImageData> images = new ArrayList<>();

        for (XSLFShape shape : slide.getShapes()) {
            if (shape instanceof XSLFTextShape) {
                extractText((XSLFTextShape) shape, texts);
            } else if (shape instanceof XSLFPictureShape) {
                extractImage((XSLFPictureShape) shape, images);
            }
        }

        return new SlideContent(slideNumber, texts, images);
    }

    private void extractText(XSLFTextShape textShape, List<String> texts) {
        String text = textShape.getText();
        if (text != null && !text.trim().isEmpty()) {
            texts.add(text.trim());
        }
    }

    private void extractImage(XSLFPictureShape picture, List<ImageData> images) {
        try {
            XSLFPictureData pictureData = picture.getPictureData();
            byte[] imageBytes = pictureData.getData();
            String contentType = pictureData.getContentType();
            String format = ImageUtils.getFormatFromContentType(contentType);
            String base64 = Base64.getEncoder().encodeToString(imageBytes);

            images.add(new ImageData(base64, format, imageBytes.length));
        } catch (Exception e) {
            logger.warn("Failed to extract image: {}", e.getMessage());
        }
    }
}