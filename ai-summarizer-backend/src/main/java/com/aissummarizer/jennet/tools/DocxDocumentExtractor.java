package com.aissummarizer.jennet.tools;

import com.aissummarizer.jennet.model.domain.DocxDocumentContent;
import com.aissummarizer.jennet.model.domain.ImageData;
import com.aissummarizer.jennet.model.domain.TableData;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class DocxDocumentExtractor {

    public DocxDocumentExtractor() {

    }

    /**
     * Extract all content from a MultipartFile (Spring Boot file upload)
     *
     * @param multipartFile The uploaded DOCX file
     * @return DocxContent containing all text and images
     * @throws Exception if file cannot be read
     */
    public static DocxDocumentContent extractContent(MultipartFile multipartFile) throws Exception {
        try (InputStream is = multipartFile.getInputStream();
             XWPFDocument document = new XWPFDocument(is)) {
            return extractFromDocument(document);
        }
    }

    /**
     * Extract all content from a File
     *
     * @param docxFile The DOCX file to process
     * @return DocxContent containing all text and images
     * @throws Exception if file cannot be read
     */
    public static DocxDocumentContent extractContent(File docxFile) throws Exception {
        try (FileInputStream fis = new FileInputStream(docxFile);
             XWPFDocument document = new XWPFDocument(fis)) {
            return extractFromDocument(document);
        }
    }

    /**
     * Extract all content from an InputStream
     *
     * @param inputStream The input stream containing DOCX data
     * @return DocxContent containing all text and images
     * @throws Exception if file cannot be read
     */
    public static DocxDocumentContent extractContent(InputStream inputStream) throws Exception {
        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            return extractFromDocument(document);
        }
    }

    /**
     * Core extraction logic from XWPFDocument
     */
    private static DocxDocumentContent extractFromDocument(XWPFDocument document) throws Exception {
        DocxDocumentContent content = new DocxDocumentContent(null, null, null);

        // Extract paragraphs
        List<XWPFParagraph> paragraphs = document.getParagraphs();
        for (XWPFParagraph paragraph : paragraphs) {
            String text = paragraph.getText();
            if (text != null && !text.trim().isEmpty()) {
                content.addParagraph(text);
            }
            System.out.println("text is " + text);
            // Extract images from paragraph runs
            extractImagesFromParagraph(paragraph, content);
        }



        // Extract tables
        List<XWPFTable> tables = document.getTables();
        for (int tableIndex = 0; tableIndex < tables.size(); tableIndex++) {
            XWPFTable table = tables.get(tableIndex);
            TableData tableData = extractTable(table, tableIndex + 1);
            content.addTable(tableData);
        }

        // Extract images from document
        List<XWPFPictureData> pictures = document.getAllPictures();
        for (XWPFPictureData picture : pictures) {
            // Skip if already extracted from paragraphs
            // This handles images that might be in headers/footers
            byte[] imageBytes = picture.getData();
            String contentType = picture.getPackagePart().getContentType();
            String base64Image = Base64.getEncoder().encodeToString(imageBytes);
            String imageFormat = getImageFormat(contentType);

            ImageData imageData = new ImageData(
                    base64Image,
                    imageFormat,
                    imageBytes.length
            );

            // Only add if not duplicate
            if (!content.hasImage(base64Image)) {
                content.addImage(imageData);
            }
        }

        return content;
    }

    /**
     * Extract images from paragraph runs
     */
    private static void extractImagesFromParagraph(XWPFParagraph paragraph, DocxDocumentContent content) {
        for (XWPFRun run : paragraph.getRuns()) {
            List<XWPFPicture> pictures = run.getEmbeddedPictures();
            for (XWPFPicture picture : pictures) {
                XWPFPictureData pictureData = picture.getPictureData();
                byte[] imageBytes = pictureData.getData();
                String contentType = pictureData.getPackagePart().getContentType();

                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                String imageFormat = getImageFormat(contentType);

                ImageData imageData = new ImageData(
                        base64Image,
                        imageFormat,
                        imageBytes.length
                );

                content.addImage(imageData);
            }
        }
    }

    /**
     * Extract table data
     */
    private static TableData extractTable(XWPFTable table, int tableNumber) {
        TableData tableData = new TableData(tableNumber, new ArrayList<>());

        List<XWPFTableRow> rows = table.getRows();
        for (XWPFTableRow row : rows) {
            List<String> rowData = new ArrayList<>();
            List<XWPFTableCell> cells = row.getTableCells();

            for (XWPFTableCell cell : cells) {
                String cellText = cell.getText();
                rowData.add(cellText != null ? cellText : "");
            }

            tableData.addRow(rowData);
        }

        return tableData;
    }

    /**
     * Get image format from content type
     */
    private static String getImageFormat(String contentType) {
        if (contentType.contains("png")) return "png";
        if (contentType.contains("jpeg") || contentType.contains("jpg")) return "jpeg";
        if (contentType.contains("gif")) return "gif";
        if (contentType.contains("webp")) return "webp";
        if (contentType.contains("bmp")) return "bmp";
        return "png"; // default
    }

//    // Example usage
//    public static void main(String[] args) {
//        try {
//            File docxFile = new File("path/to/your/document.docx");
//            DocxContent content = extractContent(docxFile);
//
//            System.out.println("=== DOCX EXTRACTION RESULTS ===\n");
//            System.out.println("Total paragraphs: " + content.getParagraphs().size());
//            System.out.println("Total tables: " + content.getTables().size());
//            System.out.println("Total images: " + content.getImages().size());
//            System.out.println();
//
//            // Display paragraphs
//            System.out.println("--- PARAGRAPHS ---");
//            for (int i = 0; i < Math.min(5, content.getParagraphs().size()); i++) {
//                System.out.println(content.getParagraphs().get(i));
//            }
//            System.out.println();
//
//            // Display tables
//            System.out.println("--- TABLES ---");
//            for (TableData table : content.getTables()) {
//                System.out.println("Table " + table.getTableNumber() +
//                        " (" + table.getRowCount() + " rows)");
//            }
//            System.out.println();
//
//            // Display images
//            System.out.println("--- IMAGES ---");
//            for (DocxImageData img : content.getImages()) {
//                System.out.println("Image: " + img.getFormat() +
//                        " (" + img.getSizeBytes() + " bytes)");
//            }
//
//        } catch (Exception e) {
//            System.err.println("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//    }
}