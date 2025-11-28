package com.aissummarizer.jennet.summarization.service;

import com.aissummarizer.jennet.config.AiSummarizerConfig;
import com.aissummarizer.jennet.common.exception.AiSummarizationException;
import com.aissummarizer.jennet.document.entity.DocumentUploadEntity;
import com.aissummarizer.jennet.document.enums.DocumentType;
import com.aissummarizer.jennet.document.service.DocumentContent;
import com.aissummarizer.jennet.summarization.entity.SummarizationEntity;
import com.aissummarizer.jennet.summarization.entity.SummaryMetadataEntity;
import com.aissummarizer.jennet.summarization.entity.SummaryResultEntity;
import com.aissummarizer.jennet.summarization.model.SummaryClasses;
import com.aissummarizer.jennet.summarization.model.SummaryOptions;
import com.aissummarizer.jennet.summarization.model.SummaryMetadata;
import com.aissummarizer.jennet.summarization.model.SummaryResult;
import com.aissummarizer.jennet.document.tools.PromptBuilder;
import com.aissummarizer.jennet.document.model.DocxDocumentContent;
import com.aissummarizer.jennet.document.dto.ImageData;
import com.aissummarizer.jennet.document.model.PptxDocumentContent;
import com.aissummarizer.jennet.document.model.TxtDocumentContent;
import com.aissummarizer.jennet.summarization.repository.SummarizationRepository;
import com.aissummarizer.jennet.summarization.repository.SummaryMetadataRepository;
import com.aissummarizer.jennet.summarization.repository.SummaryResultRepository;
import com.aissummarizer.jennet.summarization.util.ChatCompletionResponseExtractor;
import com.aissummarizer.jennet.user.entity.UserEntity;
import com.aissummarizer.jennet.user.service.UserService;
import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final SummarizationRepository summarizationRepository;

    @Autowired
    public OpenAiSummarizer(
            OpenAIClient client,
            AiSummarizerConfig config,
            PromptBuilder promptBuilder,
            SummarizationServiceImpl service, SummaryMetadataRepository metadataRepository, SummaryResultRepository summaryResultRepository, UserService userService, SummarizationRepository summarizationRepository) {
        this.client = client;
        this.config = config;
        this.promptBuilder = promptBuilder;
        this.summarizationService = service;
        this.metadataRepository = metadataRepository;
        this.summaryResultRepository = summaryResultRepository;
        this.userService = userService;
        this.summarizationRepository = summarizationRepository;
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

            // Build metadata model
            SummaryMetadata metadata = buildMetadata(content, startTime);

            UserEntity user = userService.getByUserName(userName);

            // Create and persist summarization first (owner of neither result nor metadata)
            SummarizationEntity summarization = new SummarizationEntity();
            summarization.setId(UUID.randomUUID().toString());
            summarization.setUser(user);
            summarization.setDocumentUpload(uploadEntity);
            summarization.setInputText(prompt);
            summarization.setSummaryText(summary);
            summarization.setDocumentType(content.getType());
            summarization.setSummaryType(options.getType());
            summarization.setCreatedAt(LocalDateTime.now());

            summarization = summarizationRepository.save(summarization);
//             optional: summarizationRepository.flush();

            SummaryResultEntity result = new SummaryResultEntity();
            result.setId(UUID.randomUUID().toString());
            result.setSummarization(summarization); // owning side set before save
            result.setSummary(summary);
            result.setDocumentType(content.getType());
            result.setSummaryType(options.getType());
            result = summaryResultRepository.save(result);
//             optional: summaryResultRepository.flush();

            // Create metadata (owning side for both summary_result_id and summarization_id)
            SummaryMetadataEntity metadataEntity = new SummaryMetadataEntity();
            metadataEntity.setId(UUID.randomUUID().toString());
            metadataEntity.setSummarization(summarization);
            metadataEntity.setWordCount(metadata.getWordCount());
            metadataEntity.setImageCount(metadata.getImageCount());
            metadataEntity.setSlideCount(metadata.getSlideCount());
            metadataEntity.setParagraphCount(metadata.getParagraphCount());
            metadataEntity.setTableCount(metadata.getTableCount());
            metadataEntity.setProcessingTime(metadata.getProcessingTimeMs());
            metadataEntity.setSummaryResult(result);
            metadataEntity = metadataRepository.save(metadataEntity);
//             optional: metadataRepository.flush();

            // update inverse sides in memory for consistency
            summarization.setMetadata(metadataEntity);
            summarization.setResult(result);
            summarizationService.saveSummarization(summarization);

            logger.info("Summary before:");
            logger.info(content.getAllText());
            logger.info("Summary from openAI");
            logger.info(summary);

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

    @Override
    public SummaryResult summarize(String customText, SummaryOptions options, String userName) throws AiSummarizationException {
        try {
            long startTime = System.currentTimeMillis();

            // Build prompt
            String prompt = promptBuilder.buildPrompt(customText, options);

            // Create message parts
            List<ChatCompletionContentPart> contentParts = createContentParts(prompt);

            // Call OpenAI API
            String summary = callOpenAi(contentParts, options);

            // Build metadata model
            SummaryMetadata metadata = buildMetadata(customText, startTime);

            UserEntity user = userService.getByUserName(userName);

            // Create and persist summarization first (owner of neither result nor metadata)
            SummarizationEntity summarization = new SummarizationEntity();
            summarization.setId(UUID.randomUUID().toString());
            summarization.setUser(user);
            summarization.setInputText(prompt);
            summarization.setSummaryText(summary);
            summarization.setSummaryType(options.getType());
            summarization.setCreatedAt(LocalDateTime.now());
            summarization.setDocumentType(DocumentType.TXT);

            summarization = summarizationRepository.save(summarization);
//             optional: summarizationRepository.flush();

            SummaryResultEntity result = new SummaryResultEntity();
            result.setId(UUID.randomUUID().toString());
            result.setSummarization(summarization); // owning side set before save
            result.setSummary(summary);
            result.setSummaryType(options.getType());
            result.setDocumentType(DocumentType.TXT);
            result = summaryResultRepository.save(result);

//             optional: summaryResultRepository.flush();

            // Create metadata (owning side for both summary_result_id and summarization_id)
            SummaryMetadataEntity metadataEntity = new SummaryMetadataEntity();
            metadataEntity.setId(UUID.randomUUID().toString());
            metadataEntity.setSummarization(summarization);
            metadataEntity.setWordCount(metadata.getWordCount());
            metadataEntity.setImageCount(metadata.getImageCount());
            metadataEntity.setSlideCount(metadata.getSlideCount());
            metadataEntity.setParagraphCount(metadata.getParagraphCount());
            metadataEntity.setTableCount(metadata.getTableCount());
            metadataEntity.setProcessingTime(metadata.getProcessingTimeMs());
            metadataEntity.setSummaryResult(result);
            metadataEntity = metadataRepository.save(metadataEntity);
//             optional: metadataRepository.flush();

            // update inverse sides in memory for consistency
            summarization.setMetadata(metadataEntity);
            summarization.setResult(result);
            summarizationService.saveSummarization(summarization);

            logger.info("Summary before:");
            logger.info(customText);
            logger.info("Summary from openAI");
            logger.info(summary);

            return new SummaryResult(
                    summary,
                    DocumentType.TXT,
                    options.getType(),
                    metadata
            );

        } catch (Exception e) {
            logger.error("Failed to summarize document", e);
            throw new AiSummarizationException("AI summarization failed", e);
        }
    }

    private void save(SummarizationEntity summarization, SummaryMetadataEntity metadata, SummaryResultEntity summaryResultEntity) {
        summarizationService.saveSummarization(summarization);
        metadataRepository.save(metadata);
        summaryResultRepository.save(summaryResultEntity);
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

        logger.info("createContentParts () method prompt value: " + prompt);
//        logger.info(parts.getFirst().text().toString());



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

    private List<ChatCompletionContentPart> createContentParts(
            String prompt) {

        List<ChatCompletionContentPart> parts = new ArrayList<>();

        // Add text



        parts.add(
                ChatCompletionContentPart.ofText(
                        ChatCompletionContentPartText.builder()
                                .text(prompt)
                                .build()
                )
        );

        return parts;
    }

    /**
     * Calls the OpenAI ChatCompletions API and extracts the response content.
     * <p>
     * This method demonstrates how to:
     * <ol>
     *   <li>Build a user message with content parts (text and images)</li>
     *   <li>Configure request parameters (model, max tokens, temperature)</li>
     *   <li>Send the request to the OpenAI API</li>
     *   <li>Extract the response content using {@link ChatCompletionResponseExtractor}</li>
     * </ol>
     * </p>
     *
     * @param contentParts the content parts to send to the API (text and optional images)
     * @param options      the summary options containing model configuration
     * @return the extracted text response from the ChatCompletion
     */
    private String callOpenAi(
            List<ChatCompletionContentPart> contentParts,
            SummaryOptions options) {

        // Step 1: Build the user message with content parts
        ChatCompletionUserMessageParam userMessage = ChatCompletionUserMessageParam.builder()
                .content(ChatCompletionUserMessageParam.Content.ofArrayOfContentParts(contentParts))
                .build();

        // Step 2: Configure the request parameters
        ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
                .model(config.getModel())
                .addMessage(userMessage)
                .maxCompletionTokens(options.getMaxTokens())
                .temperature(options.getTemperature())
                .build();

        // Step 3: Send the request to OpenAI and get the ChatCompletion response
        ChatCompletion completion = client.chat().completions().create(params);

        // Step 4: Extract the response content using the utility class
        // The response is contained in completion.choices().get(index).message().content()
        // ChatCompletionResponseExtractor provides a clean way to extract this
        return ChatCompletionResponseExtractor.extractContent(completion);
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

    private SummaryMetadata buildMetadata(String content, long startTime) {
        SummaryMetadata.Builder builder = SummaryMetadata.builder()
                .wordCount(content.getBytes().length)
                .imageCount(0)
                .processingTimeMs(System.currentTimeMillis() - startTime);
        return builder.build();
    }
}
