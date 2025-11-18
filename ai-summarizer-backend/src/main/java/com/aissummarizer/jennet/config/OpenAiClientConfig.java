package com.aissummarizer.jennet.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAiClientConfig {

    @Bean
    public OpenAIClient openAiClient(AiSummarizerConfig config) {
        return OpenAIOkHttpClient.builder()
                .apiKey(config.getApiKey())
                .timeout(Duration.ofMillis(config.getRequestTimeoutMs()))
                .build();
    }
}
