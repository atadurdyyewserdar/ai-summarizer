package com.aissummarizer.jennet.service;

import com.aissummarizer.jennet.config.AiSummarizerConfig;
import com.aissummarizer.jennet.exceptions.AiSummarizationException;
import com.aissummarizer.jennet.model.domain.DocumentContent;
import com.aissummarizer.jennet.model.domain.DocxDocumentContent;
import com.aissummarizer.jennet.model.domain.PptxDocumentContent;
import com.aissummarizer.jennet.model.domain.TxtDocumentContent;
import com.aissummarizer.jennet.model.request.SummaryOptions;
import com.aissummarizer.jennet.model.response.SummaryMetadata;
import com.aissummarizer.jennet.model.response.SummaryResult;
import com.aissummarizer.jennet.util.PromptBuilder;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OpenAiSummarizer implements AiSummarizer {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiSummarizer.class);

    private final OpenAIClient client;
    private final AiSummarizerConfig config;
    private final PromptBuilder promptBuilder;

    @Autowired
    public OpenAiSummarizer(
            OpenAIClient client,
            AiSummarizerConfig config,
            PromptBuilder promptBuilder) {
        this.client = client;
        this.config = config;
        this.promptBuilder = promptBuilder;
    }

    @Override
    public SummaryResult summarize(DocumentContent content, SummaryOptions options) {
        try {
            long startTime = System.currentTimeMillis();

            // Build prompt
            String prompt = promptBuilder.buildPrompt(content, options);

            // Create message parts
            List<ChatCompletionContentPart> contentParts = createContentParts(prompt, content);

            // Call OpenAI API
            String summary = callOpenAi(contentParts, options);

            // Build metadata
            SummaryMetadata metadata = buildMetadata(content, startTime);

            return new SummaryResult(
                    summary,
                    content.getType(),
                    options.getType(),
                    metadata
            );

        } catch (Exception e) {
            logger.error("Failed to summarize document", e);
            throw new AiSummarizationException("AI summarization failed", e);
        }
    }

    private List<ChatCompletionContentPart> createContentParts(
            String prompt,
            DocumentContent content) {

        List<ChatCompletionContentPart> parts = new ArrayList<>();

        // Add text
        parts.add(ChatCompletionContentPartText.builder()
                .text(prompt)
                .build());

        // Add images if present
        if (content.hasImages()) {
            for (ImageData image : content.getImages()) {
                parts.add(ChatCompletionContentPartImage.builder()
                        .imageUrl(ChatCompletionContentPartImage.ImageUrl.builder()
                                .url(image.getDataUrl())
                                .build())
                        .build());
            }
        }

        return parts;
    }

    private String callOpenAi(
            List<ChatCompletionContentPart> contentParts,
            SummaryOptions options) {

        ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
                .content(ChatCompletionUserMessageParam.Content.ofArrayOfContentParts(contentParts))
                .build();

        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(config.getModel())
                .addMessage(userMessage)
                .maxCompletionTokens(options.getMaxTokens())
                .temperature(options.getTemperature())
                .build();

        ChatCompletion completion = client.chat().completions().create(params);
        return completion.choices().get(0).message().content().orElse("");
    }

    private SummaryMetadata buildMetadata(DocumentContent content, long startTime) {
        SummaryMetadata.Builder builder = SummaryMetadata.builder()
                .wordCount(content.getWordCount())
                .imageCount(content.getImages().size())
                .processingTimeMs(System.currentTimeMillis() - startTime);

        if (content instanceof PptxDocumentContent) {
            PptxDocumentContent pptx = (PptxDocumentContent) content;
            builder.slideCount(pptx.getSlides().size());
        } else if (content instanceof DocxDocumentContent) {
            DocxDocumentContent docx = (DocxDocumentContent) content;
            builder.paragraphCount(docx.getParagraphs().size())
                    .tableCount(docx.getTables().size());
        } else if (content instanceof TxtDocumentContent) {
            TxtDocumentContent txt = (TxtDocumentContent) content;
            builder.paragraphCount(txt.getParagraphs().size());
        }

        return builder.build();
    }
}
