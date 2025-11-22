package com.aissummarizer.jennet.document.extractor;

import com.aissummarizer.jennet.common.exception.DocumentProcessingException;
import com.aissummarizer.jennet.document.service.DocumentExtractor;
import com.aissummarizer.jennet.document.model.DocxDocumentContent;
import com.aissummarizer.jennet.document.dto.ImageData;
import com.aissummarizer.jennet.document.dto.TableData;
import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.document.tools.ImageUtils;
import org.apache.poi.xwpf.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DocxDocumentExtractor implements DocumentExtractor<DocxDocumentContent> {

    private static final Logger logger = LoggerFactory.getLogger(DocxDocumentExtractor.class);

    @Override
    public DocxDocumentContent extract(InputStream inputStream)
            throws DocumentProcessingException {

        try (XWPFDocument document = new XWPFDocument(inputStream)) {
            return extractFromDocument(document);
        } catch (IOException e) {
            throw new DocumentProcessingException("Failed to extract DOCX content", e);
        }
    }

    @Override
    public boolean supports(String fileExtension) {
        return "docx".equalsIgnoreCase(fileExtension);
    }

    @Override
    public DocumentType getDocumentType() {
        return DocumentType.DOCX;
    }

    private DocxDocumentContent extractFromDocument(XWPFDocument document) {
        List<String> paragraphs = extractParagraphs(document);
        List<TableData> tables = extractTables(document);
        List<ImageData> images = extractImages(document);

        logger.info("Extracted {} paragraphs, {} tables, {} images from DOCX",
                paragraphs.size(), tables.size(), images.size());

        return new DocxDocumentContent(paragraphs, tables, images);
    }

    private List<String> extractParagraphs(XWPFDocument document) {
        return document.getParagraphs().stream()
                .map(XWPFParagraph::getText)
                .filter(text -> text != null && !text.trim().isEmpty())
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private List<TableData> extractTables(XWPFDocument document) {
        List<TableData> tables = new ArrayList<>();

        for (int i = 0; i < document.getTables().size(); i++) {
            XWPFTable table = document.getTables().get(i);
            tables.add(extractTable(table, i + 1));
        }

        return tables;
    }

    private TableData extractTable(XWPFTable table, int tableNumber) {
        List<List<String>> rows = new ArrayList<>();

        for (XWPFTableRow row : table.getRows()) {
            List<String> cells = row.getTableCells().stream()
                    .map(XWPFTableCell::getText)
                    .collect(Collectors.toList());
            rows.add(cells);
        }

        return new TableData(tableNumber, rows);
    }

    private List<ImageData> extractImages(XWPFDocument document) {
        List<ImageData> images = new ArrayList<>();

        for (XWPFPictureData picture : document.getAllPictures()) {
            try {
                byte[] imageBytes = picture.getData();
                String contentType = picture.getPackagePart().getContentType();
                String format = ImageUtils.getFormatFromContentType(contentType);
                String base64 = Base64.getEncoder().encodeToString(imageBytes);

                images.add(new ImageData(base64, format, imageBytes.length));
            } catch (Exception e) {
                logger.warn("Failed to extract image: {}", e.getMessage());
            }
        }

        return images;
    }
}