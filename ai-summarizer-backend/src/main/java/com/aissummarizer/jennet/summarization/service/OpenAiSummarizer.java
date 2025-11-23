package com.aissummarizer.jennet.summarization.service;

import com.aissummarizer.jennet.config.AiSummarizerConfig;
import com.aissummarizer.jennet.common.exception.AiSummarizationException;
import com.aissummarizer.jennet.document.entity.DocumentUploadEntity;
import com.aissummarizer.jennet.document.service.DocumentContent;
import com.aissummarizer.jennet.summarization.entity.SummarizationEntity;
import com.aissummarizer.jennet.summarization.entity.SummaryMetadataEntity;
import com.aissummarizer.jennet.summarization.entity.SummaryResultEntity;
import com.aissummarizer.jennet.summarization.model.SummaryOptions;
import com.aissummarizer.jennet.summarization.model.SummaryMetadata;
import com.aissummarizer.jennet.summarization.model.SummaryResult;
import com.aissummarizer.jennet.document.tools.PromptBuilder;
import com.aissummarizer.jennet.document.model.DocxDocumentContent;
import com.aissummarizer.jennet.document.dto.ImageData;
import com.aissummarizer.jennet.document.model.PptxDocumentContent;
import com.aissummarizer.jennet.document.model.TxtDocumentContent;
import com.aissummarizer.jennet.summarization.repository.SummaryMetadataRepository;
import com.aissummarizer.jennet.summarization.repository.SummaryResultRepository;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.user.service.UserService;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OpenAiSummarizer implements AiSummarizer {

    private static final Logger logger = LoggerFactory.getLogger(OpenAiSummarizer.class);

    private final OpenAIClient client;
    private final AiSummarizerConfig config;
    private final PromptBuilder promptBuilder;
    private final SummarizationServiceImpl summarizationService;
    private final SummaryMetadataRepository metadataRepository;
    private final SummaryResultRepository summaryResultRepository;
    private final UserService userService;

    @Autowired
    public OpenAiSummarizer(
            OpenAIClient client,
            AiSummarizerConfig config,
            PromptBuilder promptBuilder,
            SummarizationServiceImpl service, SummaryMetadataRepository metadataRepository, SummaryResultRepository summaryResultRepository, UserService userService) {
        this.client = client;
        this.config = config;
        this.promptBuilder = promptBuilder;
        this.summarizationService = service;
        this.metadataRepository = metadataRepository;
        this.summaryResultRepository = summaryResultRepository;
        this.userService = userService;
    }

    @Override
    public SummaryResult summarize(DocumentContent content, SummaryOptions options, String userName, DocumentUploadEntity uploadEntity) {
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

            UserEntity user = userService.getByUsername(userName);

            SummarizationEntity summarization = new SummarizationEntity();
            summarization.setId(UUID.randomUUID().toString());
            summarization.setUser(user);
            summarization.setDocumentUpload(uploadEntity);
            summarization.setInputText(prompt);
            summarization.setSummaryText(summary);
            summarization.setDocumentType(content.getType());
            summarization.setSummaryType(options.getType());
            summarization.setCreatedAt(LocalDateTime.now());

            summarization = summarizationService.saveSummarization(summarization);

            SummaryResultEntity result = new SummaryResultEntity();
            result.setId(UUID.randomUUID().toString());
            result.setSummarization(summarization);
            result.setSummary(summary);
            result.setDocumentType(content.getType());
            result.setSummaryType(options.getType());
            result = summaryResultRepository.save(result);

            SummaryMetadataEntity metadataEntity = new SummaryMetadataEntity();
            metadataEntity.setId(UUID.randomUUID().toString());
            metadataEntity.setSummarization(summarization);
            metadataEntity.setWordCount(metadata.getWordCount());
            metadataEntity.setImageCount(metadata.getImageCount());
            metadataEntity.setSlideCount(metadata.getSlideCount());
            metadataEntity.setParagraphCount(metadata.getParagraphCount());
            metadataEntity.setTableCount(metadata.getTableCount());
            metadataEntity.setProcessingTime(metadataEntity.getProcessingTime());
            metadataEntity.setSummaryResult(result);
            metadataEntity = metadataRepository.save(metadataEntity);

            summarization.setMetadata(metadataEntity);
            summarization.setResult(result);
            summarizationService.saveSummarization(summarization);


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

        parts.add(
                ChatCompletionContentPart.ofText(
                        ChatCompletionContentPartText.builder()
                                .text(prompt)
                                .build()
                )
        );

        // Add images if present
        if (content.hasImages()) {
            for (ImageData image : content.getImages()) {
                parts.add(

                        ChatCompletionContentPart.ofImageUrl(
                                ChatCompletionContentPartImage.builder().imageUrl(
                                        ChatCompletionContentPartImage.ImageUrl.builder().url(image.getDataUrl()).build()
                                ).build()
                        ));
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
        return completion.choices().getFirst().message().content().orElse("");
    }

    private SummaryMetadata buildMetadata(DocumentContent content, long startTime) {
        SummaryMetadata.Builder builder = SummaryMetadata.builder()
                .wordCount(content.getWordCount())
                .imageCount(content.getImages().size())
                .processingTimeMs(System.currentTimeMillis() - startTime);

        if (content instanceof PptxDocumentContent pptx) {
            builder.slideCount(pptx.getSlides().size());
        } else if (content instanceof DocxDocumentContent docx) {
            builder.paragraphCount(docx.getParagraphs().size())
                    .tableCount(docx.getTables().size());
        } else if (content instanceof TxtDocumentContent txt) {
            builder.paragraphCount(txt.getParagraphs().size());
        }

        return builder.build();
    }
}
