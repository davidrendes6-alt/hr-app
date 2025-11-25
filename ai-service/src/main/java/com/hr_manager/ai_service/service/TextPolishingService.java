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
    private final String apiKey;


    public TextPolishingService(
            @Value("${huggingface.api.url}") String apiUrl,
            @Value("${huggingface.api.model}") String model,
            @Value("${huggingface.api.key}") String apiKey) {
        this.model = model;
        this.apiKey = apiKey;
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

        prompt.append("You are a professional text editor. Improve and polish the following text to make it more professional, clear, and well-structured. ");
        prompt.append("Return ONLY the polished text without any explanations, introductions, or additional comments. ");
        prompt.append("Do not include phrases like 'Here is the polished version' or similar. ");
        prompt.append("Just provide the improved text directly.\n\n");

        if (request.getContext() != null && !request.getContext().isEmpty()) {
            prompt.append("Context: ").append(request.getContext()).append("\n\n");
        }

        prompt.append("Text to polish:\n");
        prompt.append(request.getText());

        return prompt.toString();
    }

    private Mono<String> callHuggingFaceApi(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", model,
                "messages", List.of(
                        Map.of(
                                "role", "user",
                                "content", prompt
                        )
                )
        );

        return webClient.post()
                .header("Authorization", "Bearer " + apiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(Map.class)
                .timeout(Duration.ofSeconds(30))
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(2))
                        .filter(throwable -> !(throwable instanceof WebClientResponseException.BadRequest)))
                .map(this::extractFromChatResponse)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.error("HuggingFace API error: {} - {}", ex.getStatusCode(), ex.getResponseBodyAsString());
                    return Mono.error(new RuntimeException("AI model returned an error: " + ex.getMessage()));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.error("Unexpected error calling HuggingFace API", ex);
                    return Mono.error(new RuntimeException("Failed to communicate with AI model: " + ex.getMessage()));
                });
    }

    private String extractFromChatResponse(Map<?, ?> response) {
        List<?> choices = (List<?>) response.get("choices");
        Map<?, ?> firstChoice = (Map<?, ?>) choices.get(0);
        Map<?, ?> message = (Map<?, ?>) firstChoice.get("message");
        return ((String) message.get("content")).trim();
    }
}

