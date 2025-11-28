package com.aissummarizer.jennet.document.tools;

import com.aissummarizer.jennet.document.service.DocumentContent;
import com.aissummarizer.jennet.summarization.model.SummaryOptions;
import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {

    public String buildPrompt(DocumentContent content, SummaryOptions options) {
        return switch (options.getType()) {
            case COMPREHENSIVE -> buildComprehensivePrompt(content);
            case BRIEF -> buildBriefPrompt(content);
            case KEY_POINTS -> buildKeyPointsPrompt(content);
            case EXECUTIVE -> buildExecutivePrompt(content);
            case SENTIMENT -> buildSentimentPrompt(content);
            case TECHNICAL -> buildTechnicalPrompt(content);
            case CUSTOM -> buildCustomPrompt(content, options.getCustomPrompt());
        };
    }

    public String buildPrompt(String content, SummaryOptions options) {
        return switch (options.getType()) {
            case COMPREHENSIVE -> buildComprehensivePrompt(content);
            case BRIEF -> buildBriefPrompt(content);
            case KEY_POINTS -> buildKeyPointsPrompt(content);
            case EXECUTIVE -> buildExecutivePrompt(content);
            case SENTIMENT -> buildSentimentPrompt(content);
            case TECHNICAL -> buildTechnicalPrompt(content);
            case CUSTOM -> buildCustomPrompt(content, options.getCustomPrompt());
        };
    }

    private String buildComprehensivePrompt(DocumentContent content) {
        return String.format("""
            Please provide a comprehensive summary of this %s.
            Include:
            1. Main topic/theme
            2. Key points covered
            3. Visual elements analysis (if any)
            4. Important takeaways
            5. Overall structure
            
            Content:
            %s
            """, content.getType().getDescription(), content.getAllText());
    }

    private String buildBriefPrompt(DocumentContent content) {
        return String.format("""
            Summarize this %s in 2-3 sentences.
            Focus only on the most important information.
            
            Content:
            %s
            """, content.getType().getDescription(), content.getAllText());
    }

    private String buildKeyPointsPrompt(DocumentContent content) {
        return String.format("""
            Extract the key points from this %s.
            Present them as a bulleted list.
            
            Content:
            %s
            """, content.getType().getDescription(), content.getAllText());
    }

    private String buildExecutivePrompt(DocumentContent content) {
        return String.format("""
            Create a professional executive summary of this %s.
            The summary should be:
            - Concise (2-3 paragraphs)
            - Written in formal business language
            - Focused on key decisions and recommendations
            
            Content:
            %s
            """, content.getType().getDescription(), content.getAllText());
    }

    private String buildSentimentPrompt(DocumentContent content) {
        return String.format("""
            Analyze the sentiment and tone of this %s.
            Describe:
            1. Overall sentiment (positive, negative, neutral)
            2. Tone (formal, informal, technical, etc.)
            3. Emotional elements
            
            Content:
            %s
            """, content.getType().getDescription(), content.getAllText());
    }

    private String buildTechnicalPrompt(DocumentContent content) {
        return String.format("""
            Provide a technical summary of this %s.
            Focus on:
            1. Technical specifications
            2. Implementation details
            3. Key algorithms or processes
            4. Technical requirements
            
            Content:
            %s
            """, content.getType().getDescription(), content.getAllText());
    }

    private String buildCustomPrompt(DocumentContent content, String customPrompt) {
        return String.format("""
            %s
            
            Content:
            %s
            """, customPrompt, content.getAllText());
    }

    private String buildComprehensivePrompt(String content) {
        return String.format("""
            Please provide a comprehensive summary of this text.
            Include:
            1. Main topic/theme
            2. Key points covered
            3. Visual elements analysis (if any)
            4. Important takeaways
            5. Overall structure
            
            Content:
            %s
            """, content);
    }

    private String buildBriefPrompt(String content) {
        return String.format("""
            Summarize this text in 2-3 sentences.
            Focus only on the most important information.
            
            Content:
            %s
            """, content);
    }

    private String buildKeyPointsPrompt(String content) {
        return String.format("""
            Extract the key points from this text.
            Present them as a bulleted list.
            
            Content:
            %s
            """, content);
    }

    private String buildExecutivePrompt(String content) {
        return String.format("""
            Create a professional executive summary of this text.
            The summary should be:
            - Concise (2-3 paragraphs)
            - Written in formal business language
            - Focused on key decisions and recommendations
            
            Content:
            %s
            """, content);
    }

    private String buildSentimentPrompt(String content) {
        return String.format("""
            Analyze the sentiment and tone of this text.
            Describe:
            1. Overall sentiment (positive, negative, neutral)
            2. Tone (formal, informal, technical, etc.)
            3. Emotional elements
            
            Content:
            %s
            """, content);
    }

    private String buildTechnicalPrompt(String content) {
        return String.format("""
            Provide a technical summary of this text.
            Focus on:
            1. Technical specifications
            2. Implementation details
            3. Key algorithms or processes
            4. Technical requirements
            
            Content:
            %s
            """, content);
    }

    private String buildCustomPrompt(String content, String customPrompt) {
        return String.format("""
            %s
            
            Content:
            %s
            """, customPrompt, content);
    }
}
