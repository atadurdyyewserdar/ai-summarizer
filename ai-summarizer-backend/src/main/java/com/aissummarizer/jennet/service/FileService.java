package com.aissummarizer.jennet.service;
import com.aissummarizer.jennet.model.*;
import com.aissummarizer.jennet.model.domain.*;
import com.aissummarizer.jennet.tools.DocxExtractor;
import com.aissummarizer.jennet.tools.PptxExtractor;
import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.*;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.aissummarizer.jennet.tools.TxtExtractor.extractContent;

@Service
public class FileService {

    @Autowired
    private final PptxDocumentContent pptxDocumentContent;

    @Autowired
    private final DocxDocumentContent docxDocumentContent;

    private static final OpenAIClient client = OpenAIOkHttpClient.fromEnv();

    public FileService(PptxDocumentContent pptxDocumentContent, DocxDocumentContent docxDocumentContent) {
        this.pptxDocumentContent = pptxDocumentContent;
        this.docxDocumentContent = docxDocumentContent;
    }

    public void DOCXExtract(MultipartFile file) {
        try {
            // Step 1: Extract content from DOCX
            System.out.println("=== EXTRACTING DOCX CONTENT ===");
            DocxDocumentContent content = DocxExtractor.extractContent(file);

            System.out.println("Extracted:");
            System.out.println("- " + content.getParagraphs().size() + " paragraphs");
            System.out.println("- " + content.getTables().size() + " tables");
            System.out.println("- " + content.getImages().size() + " images");
            System.out.println();

            // Step 2: Send to OpenAI for summarization
            System.out.println("=== SENDING TO OPENAI ===");

            // Option 1: General summary
            String summary = defaultSummarizeDocx(content);
            System.out.println("\n=== SUMMARY ===\n");
            System.out.println(summary);

            // Option 2: Executive summary
            // String execSummary = summarizer.generateExecutiveSummary(content);
            // System.out.println("\n=== EXECUTIVE SUMMARY ===\n");
            // System.out.println(execSummary);

            // Option 3: Extract key information
            // String keyInfo = summarizer.extractKeyInfo(content);
            // System.out.println("\n=== KEY INFORMATION ===\n");
            // System.out.println(keyInfo);

            // Option 4: Analyze tables
            // String tableAnalysis = summarizer.analyzeTables(content);
            // System.out.println("\n=== TABLE ANALYSIS ===\n");
            // System.out.println(tableAnalysis);

            // Option 5: Ask specific question
            // String answer = summarizer.askQuestion(content, "What are the main recommendations?");
            // System.out.println("\n=== ANSWER ===\n");
            // System.out.println(answer);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Summarize DOCX content using OpenAI
     *
     * @param content The extracted DOCX content
     * @return AI-generated summary
     */
    public String summarizeDocx(DocxDocumentContent content) {
        return summarizeDocx(content, getDefaultPrompt());
    }

    public String summarizeDocx(DocxDocumentContent content, String customPrompt) {
        // Build the prompt with formatted content
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(customPrompt).append("\n\n");
        promptBuilder.append(content.getFormattedContent());

        // Create content parts list
        List<ChatCompletionContentPart> contentParts = new ArrayList<>();

        // Add text content part
        contentParts.add(
                ChatCompletionContentPart.ofText(
                        ChatCompletionContentPartText.builder()
                                .text(promptBuilder.toString())
                                .build()
                )
        );

        // Add all images as content parts
        List<ImageData> allImages = content.getImages();
        for (ImageData image : allImages) {
            contentParts.add(
                    ChatCompletionContentPart.ofImageUrl(
                            ChatCompletionContentPartImage.builder().imageUrl(
                                    ChatCompletionContentPartImage.ImageUrl.builder().url(image.getDataUrl()).build()
                            ).build()
                    )
            );
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
        System.out.println("- Paragraphs: " + content.getParagraphs().size());
        System.out.println("- Tables: " + content.getTables().size());
        System.out.println("- Images: " + allImages.size());

        ChatCompletion completion = client.chat().completions().create(params);

        String response = completion.choices().getFirst().message().content().orElse("");
        System.out.println("Received response from OpenAI");

        return response;
    }

    /**
     * Get default summarization prompt
     */
    private String getDefaultPromptDocx() {
        return "Please provide a comprehensive summary of this Word document. " +
                "Analyze the text, tables, and images provided. " +
                "Include:\n" +
                "1. Main topic/theme of the document\n" +
                "2. Key points and arguments\n" +
                "3. Analysis of tables and data presented\n" +
                "4. Description of visual elements (charts, diagrams, images)\n" +
                "5. Important conclusions and takeaways";
    }

    /**
     * Extract key information from the document
     */
    public String extractKeyInfoDocx(DocxDocumentContent docxDocumentContent) {
        String prompt = "Please extract and list the key information from this document. " +
                "Focus on:\n" +
                "1. Main facts and figures\n" +
                "2. Important names, dates, and locations\n" +
                "3. Key statistics from tables\n" +
                "4. Action items or recommendations\n" +
                "Present this as a bulleted list.";

        return summarizeDocx(docxDocumentContent, prompt);
    }

    /**
     * Analyze tables in the document
     */
    public String analyzeTablesDocx(DocxDocumentContent content) {
        if (content.getTables().isEmpty()) {
            return "No tables found in the document.";
        }

        String prompt = "Please analyze the tables in this document. " +
                "For each table:\n" +
                "1. Describe what data it contains\n" +
                "2. Identify trends or patterns\n" +
                "3. Highlight important values\n" +
                "4. Explain the significance of the data";

        return summarizeDocx(content, prompt);
    }

    /**
     * Ask a custom question about the document
     */
    public String askQuestionDocx(DocxDocumentContent content, String question) {
        String prompt = "Based on this document, please answer the following question:\n\n" +
                question + "\n\n" +
                "Document content:\n";

        return summarizeDocx(content, prompt);
    }

    public String defaultSummarizeDocx(DocxDocumentContent content) throws IOException {
        String prompt = "Please, tell me what is this document about. I have extracted text for you.:\n\n" +
                "Document content:\n" +
                content.getAllText();
        return summarizeDocx(content, prompt);
    }

    /**
     * Generate a professional summary for business use
     */
    public String generateExecutiveSummaryDocx(DocxDocumentContent content) {
        String prompt = "Create a professional executive summary of this document. " +
                "The summary should be:\n" +
                "- Concise (2-3 paragraphs maximum)\n" +
                "- Written in formal business language\n" +
                "- Focused on key decisions, findings, and recommendations\n" +
                "- Suitable for senior management review";

        return summarizeDocx(content, prompt);
    }


    public void PPTXExtract (MultipartFile file) throws Exception {
        PptxDocumentContent pptxDocumentContent1 = PptxExtractor.extractContent(file);
        System.out.println("=== PPTX EXTRACTION RESULTS ===\n");
        System.out.println("Total slides: " + pptxDocumentContent1.getSlides().size());
        System.out.println("Total images: " + pptxDocumentContent1.getTotalImageCount());
        System.out.println();

        for (SlideContent slide : pptxDocumentContent1.getSlides()) {
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
        List<ImageData> allImages = pptxDocumentContent1.getAllImages();
        System.out.println("All images extracted: " + allImages.size());

        // Get all text concatenated
        String allText = pptxDocumentContent1.getAllText();
        System.out.println("Total text length: " + allText.length() + " characters");

        System.out.println("--------------------------------------------------------");
        System.out.println(summarize(pptxDocumentContent1));
    }

    public String summarize(PptxDocumentContent content) throws IOException {
        return defaultSummarize(content);
    }

    /**
     * Summarize PPTX content with custom prompt
     *
     * @param content The extracted PPTX content
     * @param customPrompt Your custom instructions
     * @return AI-generated summary
     */

    public String summarize(PptxDocumentContent content, String customPrompt) throws IOException {
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
    public String askQuestion(PptxDocumentContent content, String question) throws IOException {
        String prompt = "Based on this presentation, please answer the following question:\n\n" +
                question + "\n\n" +
                "Presentation content:\n" +
                content.getAllText();

        return summarize(content, prompt);
    }

    public String defaultSummarize(PptxDocumentContent content) throws IOException {
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

    public void TXTExtractor(MultipartFile file) throws Exception {
        TxtDocumentContent content = extractContent(file);

        System.out.println("=== TXT EXTRACTION RESULTS ===\n");
        System.out.println("Total paragraphs: " + content.getParagraphs().size());
        System.out.println("Total characters: " + content.getAllText().length());
        System.out.println("Total words (approx): " + content.getWordCount());
        System.out.println();

        // Display first few paragraphs
        System.out.println("--- FIRST 3 PARAGRAPHS ---");
        List<String> paragraphs = content.getParagraphs();
        for (int i = 0; i < Math.min(3, paragraphs.size()); i++) {
            System.out.println("Paragraph " + (i + 1) + ":");
            System.out.println(paragraphs.get(i));
            System.out.println();
        }
        System.out.println(createBriefSummaryTxt(content));
    }

    public String summarizeTxt(TxtDocumentContent content) {
        return summarizeTxt(content, getDefaultPrompt());
    }

    /**
     * Summarize TXT content with custom prompt
     *
     * @param content The extracted TXT content
     * @param customPrompt Your custom instructions
     * @return AI-generated summary
     */
    public String summarizeTxt(TxtDocumentContent content, String customPrompt) {
        // Build the prompt with content
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(customPrompt).append("\n\n");
        promptBuilder.append(content.getFormattedContent());

        // Create content parts list (only text for TXT files)
        List<ChatCompletionContentPart> contentParts = new ArrayList<>();

        // Add text content part
        contentParts.add(
                ChatCompletionContentPart.ofText(
                        ChatCompletionContentPartText.builder()
                                .text(promptBuilder.toString())
                                .build())
                );

        // Build the user message
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
        System.out.println("- Lines: " + content.getParagraphs().size());
        System.out.println("- Words: " + content.getWordCount());

        ChatCompletion completion = client.chat().completions().create(params);

        String response = completion.choices().getFirst().message().content().orElse("");
        System.out.println("Received response from OpenAI");

        return response;
    }

    /**
     * Get default summarization prompt
     */
    private String getDefaultPromptTxt() {
        return "Please provide a comprehensive summary of this text document. " +
                "Include:\n" +
                "1. Main topic/theme\n" +
                "2. Key points and arguments\n" +
                "3. Important details and facts\n" +
                "4. Conclusions or recommendations\n" +
                "5. Overall structure and purpose";
    }

    /**
     * Create a concise summary (2-3 sentences)
     */
    public String createBriefSummaryTxt(TxtDocumentContent content) {
        String prompt = "Summarize this text sentences. " +
                "Focus only on the most important information.";

        return summarizeTxt(content, prompt);
    }

    /**
     * Extract key points as bullet list
     */
    public String extractKeyPointsTxt(TxtDocumentContent content) {
        String prompt = "Extract the key points from this text. " +
                "Present them as a bulleted list. " +
                "Focus on the most important facts, arguments, and conclusions.";

        return summarizeTxt(content, prompt);
    }

    /**
     * Analyze sentiment and tone
     */
    public String analyzeSentimentTxt(TxtDocumentContent content) {
        String prompt = "Analyze the sentiment and tone of this text. " +
                "Describe:\n" +
                "1. Overall sentiment (positive, negative, neutral)\n" +
                "2. Tone (formal, informal, technical, etc.)\n" +
                "3. Emotional elements\n" +
                "4. Author's perspective or bias";

        return summarizeTxt(content, prompt);
    }

    /**
     * Ask a custom question about the text
     */
    public String askQuestion(TxtDocumentContent content, String question) {
        String prompt = "Based on this text, please answer the following question:\n\n" +
                question;

        return summarizeTxt(content, prompt);
    }

    /**
     * Translate or rewrite in different style
     */
    public String rewriteTxt(TxtDocumentContent content, String style) {
        String prompt = "Rewrite this text in a " + style + " style. " +
                "Maintain the core message but adapt the language and tone.";

        return summarizeTxt(content, prompt);
    }

    /**
     * Generate title and tags
     */
    public String generateMetadata(TxtDocumentContent content) {
        String prompt = "Based on this text, generate:\n" +
                "1. A suitable title (one line)\n" +
                "2. 5-7 relevant tags or keywords\n" +
                "3. A one-sentence description";

        return summarizeTxt(content, prompt);
    }

}
