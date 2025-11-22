package com.aissummarizer.jennet.document.extractor;

import com.aissummarizer.jennet.common.exception.DocumentProcessingException;
import com.aissummarizer.jennet.document.service.DocumentExtractor;
import com.aissummarizer.jennet.document.dto.ImageData;
import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.document.model.PdfDocumentContent;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessRead;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Component
public class PdfDocumentExtractor implements DocumentExtractor<PdfDocumentContent> {

    private static final Logger logger = LoggerFactory.getLogger(PdfDocumentExtractor.class);
    private static final float PDF_RENDERING_SCALE = 2.0f; // Higher quality

    @Override
    public PdfDocumentContent extract(InputStream inputStream)
            throws DocumentProcessingException {

        PDDocument document = null;
        try (RandomAccessRead rar = new RandomAccessReadBuffer(inputStream)){
            // PDFBox 3.x - Use Loader.loadPDF() for InputStream
            document = Loader.loadPDF(rar);
            return extractFromDocument(document);
        } catch (IOException e) {
            throw new DocumentProcessingException("Failed to extract PDF content", e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    logger.warn("Failed to close PDF document", e);
                }
            }
        }
    }

    @Override
    public boolean supports(String fileExtension) {
        return "pdf".equalsIgnoreCase(fileExtension);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.PDF;
    }

    private PdfDocumentContent extractFromDocument(PDDocument document)
            throws DocumentProcessingException {

        try {
            List<String> textPages = extractText(document);
            List<ImageData> images = extractImages(document);
            int totalPages = document.getNumberOfPages();

            logger.info("Extracted {} pages and {} images from PDF",
                    totalPages, images.size());

            return new PdfDocumentContent(textPages, images, totalPages);

        } catch (IOException e) {
            throw new DocumentProcessingException("Failed to process PDF document", e);
        }
    }

    /**
     * Extract text from each page
     */
    private List<String> extractText(PDDocument document) throws IOException {
        List<String> textPages = new ArrayList<>();
        PDFTextStripper stripper = new PDFTextStripper();

        // Extract text page by page
        stripper.setStartPage(1);
        stripper.setEndPage(document.getNumberOfPages());

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            stripper.setStartPage(i + 1);
            stripper.setEndPage(i + 1);

            String pageText = stripper.getText(document);
            if (pageText != null && !pageText.trim().isEmpty()) {
                textPages.add(pageText.trim());
            }
        }

        logger.debug("Extracted text from {} pages", textPages.size());
        return textPages;
    }

    /**
     * Extract images from PDF by rendering pages
     */
    private List<ImageData> extractImages(PDDocument document)
            throws IOException {

        List<ImageData> images = new ArrayList<>();
        PDFRenderer renderer = new PDFRenderer(document);

        for (int i = 0; i < document.getNumberOfPages(); i++) {
            try {
                // Render page to image
                BufferedImage image = renderer.renderImage(i, PDF_RENDERING_SCALE);

                // Convert to base64
                String base64 = convertImageToBase64(image);
                images.add(new ImageData(base64, "png", getImageSize(image)));

                logger.debug("Extracted image from page {}", i + 1);

            } catch (Exception e) {
                logger.warn("Failed to extract image from page {}: {}", i + 1, e.getMessage());
            }
        }

        return images;
    }

    /**
     * Convert BufferedImage to base64 PNG
     */
    private String convertImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    /**
     * Get approximate size of image
     */
    private int getImageSize(BufferedImage image) {
        return image.getWidth() * image.getHeight() * 4; // Approximate bytes
    }
}
