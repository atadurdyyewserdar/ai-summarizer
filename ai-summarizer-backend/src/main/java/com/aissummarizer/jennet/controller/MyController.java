package com.aissummarizer.jennet.controller;

import com.aissummarizer.jennet.model.PDFPageText;
import com.aissummarizer.jennet.model.SlideContent;
import com.aissummarizer.jennet.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
public class MyController {

    private final FileService fileService;

    public MyController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/msg_text")
    public ResponseEntity<String> msgText(@RequestParam("txt")String text) {
        return ResponseEntity.ok().body("Success");
    }

    @PostMapping("/msg_pdf")
    public ResponseEntity<List<PDFPageText>> msgPDF(@RequestParam("file") MultipartFile file) throws IOException {
        List<PDFPageText>extracted = fileService.PDFExtractor(file);
        return ResponseEntity.ok().body(extracted);
    }

    @PostMapping("/msg_pptx")
    public ResponseEntity<String> msgPPTX(@RequestParam("file")MultipartFile file) throws Exception {
//        List<SlideContent> extracted = fileService.PPTXExtractor(file);
        fileService.PPTXExtract(file);
        return ResponseEntity.ok().body("Success");
    }

    @PostMapping("/msg_txt_ile")
    public ResponseEntity<String> msgTXTFile(@RequestParam("file")String file) {
        return ResponseEntity.ok().body("Success");
    }

    @PostMapping("/msg_docx")
    public ResponseEntity<String> msgDOCX(@RequestParam("file")String file) {
        return ResponseEntity.ok().body("Success");
    }
}