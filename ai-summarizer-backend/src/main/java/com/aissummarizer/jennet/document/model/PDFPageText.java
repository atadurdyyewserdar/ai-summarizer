package com.aissummarizer.jennet.document.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PDFPageText {
    private int page;
    private String text;
}
