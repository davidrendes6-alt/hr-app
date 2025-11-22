package com.hr_manager.ai_service.service;

import com.hr_manager.ai_service.dto.PolishRequest;
import com.hr_manager.ai_service.dto.PolishResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class TextPolishingService {

    private final WebClient webClient;
    private final String model;

    public TextPolishingService(
            @Value("${huggingface.api.url}") String apiUrl,
            @Value("${huggingface.api.model}") String model,
            @Value("${huggingface.api.timeout}") int timeout) {
        this.model = model;
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();

        log.info("TextPolishingService initialized with model: {}", model);
    }

    public Mono<PolishResponse> polishText(PolishRequest request) {
        log.info("Polishing text with length: {}", request.getText().length());

        // Build the prompt for the AI model
        String prompt = buildPrompt(request);

        // Call HuggingFace API
        return callHuggingFaceApi(prompt)
                .map(polishedText -> PolishResponse.builder()
                        .originalText(request.getText())
                        .polishedText(polishedText)
                        .model(model)
                        .build())
                .doOnSuccess(response -> log.info("Successfully polished text"))
                .doOnError(error -> log.error("Error polishing text: {}", error.getMessage()));
    }

    private String buildPrompt(PolishRequest request) {
        StringBuilder prompt = new StringBuilder();

        if (request.getContext() != null && !request.getContext().isEmpty()) {
            prompt.append("Context: ").append(request.getContext()).append("\n\n");
        }

        prompt.append("Please improve and polish the following text to make it more professional, clear, and well-structured: ");
        prompt.append(request.getText());

        return prompt.toString();
    }

    private Mono<String> callHuggingFaceApi(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "inputs", prompt,
                "parameters", Map.of(
                        "max_length", 512,
                        "temperature", 0.7,
                        "do_sample", true
                )
        );

        return webClient.post()
                .uri("/{model}", model)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(List.class)
                .timeout(Duration.ofSeconds(30))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(2))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest)))
                .map(this::extractTextFromResponse)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("HuggingFace API error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.error(new RuntimeException("AI model returned an error: " + ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Unexpected error calling HuggingFace API", ex);
                    return Mono.error(new RuntimeException("Failed to communicate with AI model: " + ex.getMessage()));
                });
    }

    private String extractTextFromResponse(List<?> response) {
        if (response == null || response.isEmpty()) {
            throw new RuntimeException("Empty response from AI model");
        }

        try {
            Object firstElement = response.get(0);
            if (firstElement instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) firstElement;
                String generatedText = (String) map.get("generated_text");

                if (generatedText != null && !generatedText.isEmpty()) {
                    return generatedText.trim();
                }
            }

            // Fallback: return the original response as string
            return response.get(0).toString().trim();
        } catch (Exception e) {
            log.error("Error extracting text from response", e);
            throw new RuntimeException("Failed to parse AI model response");
        }
    }
}

