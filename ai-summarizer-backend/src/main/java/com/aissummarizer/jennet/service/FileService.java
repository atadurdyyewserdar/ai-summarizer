package com.aissummarizer.jennet.service;
import com.aissummarizer.jennet.model.ImageData;
import com.aissummarizer.jennet.model.PDFPageText;
import com.aissummarizer.jennet.model.PptxContent;
import com.aissummarizer.jennet.model.SlideContent;
import com.aissummarizer.jennet.tools.PptxExtractor;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FileService {

    @Autowired
    private final PptxContent pptxContent;

    private static final OpenAIClient client = OpenAIOkHttpClient.fromEnv();

    public FileService(PptxContent pptxContent) {
        this.pptxContent = pptxContent;
    }

    public List<String>DOCXExtractor(MultipartFile file) throws IOException {
        XWPFDocument document = new XWPFDocument(file.getInputStream());
            List<XWPFParagraph> paragraphs = document.getParagraphs();
            return paragraphs.stream()
                    .map(XWPFParagraph::getText)
                    .filter(p -> !p.isBlank())
                    .collect(Collectors.toList());

    }

    public void PPTXExtract (MultipartFile file) throws Exception {
        PptxContent pptxContent1 = PptxExtractor.extractContent(file);
        System.out.println("=== PPTX EXTRACTION RESULTS ===\n");
        System.out.println("Total slides: " + pptxContent1.getSlides().size());
        System.out.println("Total images: " + pptxContent1.getTotalImageCount());
        System.out.println();

        for (SlideContent slide : pptxContent1.getSlides()) {
            System.out.println("--- Slide " + slide.getSlideNumber() + " ---");
            System.out.println("Text items: " + slide.getTextItems().size());
            System.out.println("Images: " + slide.getImages().size());

            // Print text
            for (String text : slide.getTextItems()) {
                System.out.println("  Text: " + text);
            }

            // Print image info
            for (ImageData img : slide.getImages()) {
                System.out.println("  Image: " + img.getFormat() +
                        " (" + img.getSizeBytes() + " bytes)");
            }
            System.out.println();
        }

        // Get all images across all slides
        List<ImageData> allImages = pptxContent1.getAllImages();
        System.out.println("All images extracted: " + allImages.size());

        // Get all text concatenated
        String allText = pptxContent1.getAllText();
        System.out.println("Total text length: " + allText.length() + " characters");

        System.out.println("--------------------------------------------------------");
        System.out.println(summarize(pptxContent1));
    }

    public String summarize(PptxContent content) throws IOException {
        return defaultSummarize(content);
    }

    /**
     * Summarize PPTX content with custom prompt
     *
     * @param content The extracted PPTX content
     * @param customPrompt Your custom instructions
     * @return AI-generated summary
     */

    public String summarize(PptxContent content, String customPrompt) throws IOException {
        // Build the prompt with text content
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(customPrompt).append("\n\n");
        promptBuilder.append("=== PRESENTATION TEXT CONTENT ===\n");
        promptBuilder.append(content.getAllText());

        // Add image count info
        int imageCount = content.getTotalImageCount();
        if (imageCount > 0) {
            promptBuilder.append("\n\n[This presentation contains ")
                    .append(imageCount)
                    .append(" image(s) which are included below for analysis]\n");
        }

        // Create content parts list
        List<ChatCompletionContentPart> contentParts = new ArrayList<>();

        // Add text content part
        contentParts.add(ChatCompletionContentPart.ofText(ChatCompletionContentPartText.builder().text(promptBuilder.toString()).build()));

        contentParts.add(ChatCompletionContentPart.ofText(
                ChatCompletionContentPartText.builder()
                        .text(promptBuilder
                                .toString())
                        .build()));
        // Add all images as content parts
        List<ImageData> allImages = content.getAllImages();
        for (ImageData image : allImages) {
            contentParts.add(ChatCompletionContentPart.ofImageUrl(
                    ChatCompletionContentPartImage.builder()
                            .imageUrl(ChatCompletionContentPartImage.ImageUrl
                                    .builder()
                                    .url(image.getDataUrl())
                                    .build())
                            .build()));
        }

        // Build the user message with all content parts
        ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
                .content(ChatCompletionUserMessageParam.Content.ofArrayOfContentParts(contentParts))
                .build();

        // Build the API request
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_5_NANO)
                .addMessage(userMessage)
                .build();

        // Call OpenAI API
        System.out.println("Sending request to OpenAI...");
        System.out.println("- Model: " + ChatModel.GPT_5_NANO);
        System.out.println("- Text length: " + content.getAllText().length() + " characters");
        System.out.println("- Images: " + allImages.size());

        ChatCompletion completion = client.chat().completions().create(params);

        String response = completion.choices().getFirst().message().content().orElse("");
        System.out.println("Received response from OpenAI");

        return response;
    }

    /**
     * Get default summarization prompt
     */
    private String getDefaultPrompt() {
        return "Please provide a comprehensive summary of this PowerPoint presentation. " +
                "Analyze both the text content and the images provided. " +
                "Include:\n" +
                "1. Main topic/theme of the presentation\n" +
                "2. Key points covered in each section\n" +
                "3. Description and analysis of visual elements (charts, diagrams, images)\n" +
                "4. Important takeaways and conclusions\n" +
                "5. Overall structure and flow of the presentation";
    }

    /**
     * Ask a custom question about the presentation
     */
    public String askQuestion(PptxContent content, String question) throws IOException {
        String prompt = "Based on this presentation, please answer the following question:\n\n" +
                question + "\n\n" +
                "Presentation content:\n" +
                content.getAllText();

        return summarize(content, prompt);
    }

    public String defaultSummarize(PptxContent content) throws IOException {
        String prompt = "Based on this presentation, can you briefly summarize this presentation:\n\n" +
                "Presentation content:\n" +
                content.getAllText();

        return summarize(content, prompt);
    }

    public List<PDFPageText> PDFExtractor(MultipartFile file) throws IOException {
        List<PDFPageText> pages = new ArrayList<>();
        byte[] pdfBytes = file.getBytes();
        try (PDDocument pdf = Loader.loadPDF(pdfBytes)) {

            PDFTextStripper stripper = new PDFTextStripper();
            int total = pdf.getNumberOfPages();

            for (int i = 1; i <= total; i++) {
                stripper.setStartPage(i);
                stripper.setEndPage(i);

                String text = stripper.getText(pdf).trim();
                pages.add(new PDFPageText(i, text));
            }
        }
        String p = pages.toString();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder().addUserMessage("Hello, Can you summarize this? Briefly?" + p).model(ChatModel.GPT_5_NANO).build();
        // create chat completion (synchronous)
		ChatCompletion chatCompletion = client.chat().completions().create(params);

        // extract text from the first choice
		String text = chatCompletion.choices().getFirst().message().toString();
		System.out.println("Model responded: " + text);
        return pages;
    }

    public String TXTExtractor(MultipartFile file) throws IOException {
        return new String(file.getBytes(), StandardCharsets.UTF_8);
    }

}
