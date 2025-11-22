package com.hr_manager.ai_service.controller;

import com.hr_manager.ai_service.dto.PolishRequest;
import com.hr_manager.ai_service.dto.PolishResponse;
import com.hr_manager.ai_service.service.TextPolishingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
public class PolishController {

    private final TextPolishingService textPolishingService;

    @PostMapping("/polish")
    public Mono<ResponseEntity<PolishResponse>> polishText(@Valid @RequestBody PolishRequest request) {
        log.info("Received polish request for text with length: {}", request.getText().length());

        return textPolishingService.polishText(request)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Successfully processed polish request"))
                .doOnError(error -> log.error("Error processing polish request", error));
    }
}

