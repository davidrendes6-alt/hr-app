package com.hr_manager.hr_service.controller;

import com.hr_manager.hr_service.dto.CreateFeedbackRequest;
import com.hr_manager.hr_service.dto.FeedbackResponse;
import com.hr_manager.hr_service.security.UserPrincipal;
import com.hr_manager.hr_service.service.FeedbackService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profiles/{profileId}/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getFeedbackForProfile(
            @PathVariable String profileId) {
        UUID id = UUID.fromString(profileId);
        List<FeedbackResponse> feedback = feedbackService.getFeedbackForProfile(id);
        return ResponseEntity.ok(feedback);
    }

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(
            @PathVariable String profileId,
            @Valid @RequestBody CreateFeedbackRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID id = UUID.fromString(profileId);
        FeedbackResponse feedback = feedbackService.createFeedback(id, request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(feedback);
    }
}

