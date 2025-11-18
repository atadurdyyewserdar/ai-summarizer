package com.aissummarizer.jennet.tools;

import com.aissummarizer.jennet.model.domain.PptxDocumentContent;
import com.aissummarizer.jennet.model.domain.SlideContent;
import org.apache.poi.xslf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

public class PptxExtractor {
    /**
     * Extract all content from a PPTX file
     *
     * @param pptxFile The PPTX file to process
     * @return PptxContent containing all text and images
     * @throws Exception if file cannot be read
     */

    public static PptxDocumentContent extractContent(MultipartFile pptxFile) throws Exception {
        PptxDocumentContent content;

        try (InputStream is = pptxFile.getInputStream();
             XMLSlideShow ppt = new XMLSlideShow(is)) {

            content = extractFromSlideShow(ppt);
        }
        return content;
    }

    /**
     * Extract all content from a File
     *
     * @param pptxFile The PPTX file to process
     * @return PptxContent containing all text and images
     * @throws Exception if file cannot be read
     */
    public static PptxDocumentContent extractContent(File pptxFile) throws Exception {
        PptxDocumentContent content;

        try (FileInputStream fis = new FileInputStream(pptxFile);
             XMLSlideShow ppt = new XMLSlideShow(fis)) {

            content = extractFromSlideShow(ppt);
        }

        return content;
    }

    /**
     * Extract all content from an InputStream
     *
     * @param inputStream The input stream containing PPTX data
     * @return PptxContent containing all text and images
     * @throws Exception if file cannot be read
     */
    public static PptxDocumentContent extractContent(InputStream inputStream) throws Exception {
        PptxDocumentContent content;

        try (XMLSlideShow ppt = new XMLSlideShow(inputStream)) {
            content = extractFromSlideShow(ppt);
        }

        return content;
    }

    /**
     * Core extraction logic from XMLSlideShow
     */
    private static void extractFromSlideShow(XMLSlideShow ppt) {
        PptxDocumentContent content;

        List<XSLFSlide> slides = ppt.getSlides();

        for (int slideIndex = 0; slideIndex < slides.size(); slideIndex++) {
            XSLFSlide slide = slides.get(slideIndex);
            SlideContent slideContent = new SlideContent(slideIndex + 1);

            // Extract all shapes from the slide
            for (XSLFShape shape : slide.getShapes()) {

                // Extract text from text shapes
                if (shape instanceof XSLFTextShape) {
                    XSLFTextShape textShape = (XSLFTextShape) shape;
                    String text = textShape.getText();
                    if (text != null && !text.trim().isEmpty()) {
                        slideContent.addText(text);
                    }
                }

                // Extract images from picture shapes
                if (shape instanceof XSLFPictureShape) {
                    XSLFPictureShape picture = (XSLFPictureShape) shape;
                    XSLFPictureData pictureData = picture.getPictureData();

                    // Get image data
                    byte[] imageBytes = pictureData.getData();
                    String contentType = pictureData.getContentType();

                    // Convert to base64
                    String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                    // Determine image format
                    String imageFormat = getImageFormat(contentType);

                    // Create image object
                    ImageData imageData = new ImageData(
                            base64Image,
                            imageFormat,
                            slideIndex + 1,
                            imageBytes.length
                    );

                    slideContent.addImage(imageData);
                }
            }

            content.addSlide(slideContent);
        }
    }

    /**
     * Get image format from content type
     */
    private static String getImageFormat(String contentType) {
        if (contentType.contains("png")) return "png";
        if (contentType.contains("jpeg") || contentType.contains("jpg")) return "jpeg";
        if (contentType.contains("gif")) return "gif";
        if (contentType.contains("webp")) return "webp";
        return "png"; // default
    }
}
