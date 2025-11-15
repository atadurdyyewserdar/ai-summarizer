package com.aissummarizer.jennet.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class PDFPageText {
    private int page;
    private String text;
}
