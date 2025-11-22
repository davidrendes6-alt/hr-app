package com.hr_manager.hr_service.service;

import com.hr_manager.hr_service.client.AiServiceClient;
import com.hr_manager.hr_service.dto.*;
import com.hr_manager.hr_service.entity.Feedback;
import com.hr_manager.hr_service.entity.User;
import com.hr_manager.hr_service.exception.ForbiddenException;
import com.hr_manager.hr_service.exception.ResourceNotFoundException;
import com.hr_manager.hr_service.repository.FeedbackRepository;
import com.hr_manager.hr_service.repository.UserRepository;
import com.hr_manager.hr_service.security.UserPrincipal;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;
    private final AiServiceClient aiServiceClient;

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackForProfile(UUID profileId) {
        // Verify profile exists
        userRepository.findById(profileId)
            .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        List<Feedback> feedbackList = feedbackRepository.findByProfileIdOrderByCreatedAtDesc(profileId);

        return feedbackList.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public FeedbackResponse createFeedback(UUID profileId, CreateFeedbackRequest request, UserPrincipal principal) {
        // Cannot leave feedback on own profile
        if (profileId.equals(principal.getUserId())) {
            throw new ForbiddenException("Cannot leave feedback on own profile");
        }

        // Verify profile exists
        User profileUser = userRepository.findById(profileId)
            .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        String content = request.getContent();
        boolean isPolished = false;

        // Polish with AI if requested
        if (Boolean.TRUE.equals(request.getPolishWithAI())) {
            try {
                PolishRequest polishRequest = new PolishRequest(content, "employee feedback");
                PolishResponse polishResponse = aiServiceClient.polishText(polishRequest);
                content = polishResponse.getPolishedText();
                isPolished = true;
                log.info("Feedback polished successfully using AI");
            } catch (FeignException e) {
                log.error("AI service error while polishing feedback: {}", e.getMessage());
                throw new RuntimeException("AI service is unavailable. Please try again later.");
            }
        }

        Feedback feedback = new Feedback();
        feedback.setProfileId(profileId);
        feedback.setAuthorId(principal.getUserId());
        feedback.setContent(content);
        feedback.setIsPolished(isPolished);

        Feedback savedFeedback = feedbackRepository.save(feedback);

        return mapToResponse(savedFeedback);
    }

    private FeedbackResponse mapToResponse(Feedback feedback) {
        User author = userRepository.findById(feedback.getAuthorId())
            .orElseThrow(() -> new ResourceNotFoundException("Author not found"));

        FeedbackResponse response = new FeedbackResponse();
        response.setId(feedback.getId().toString());
        response.setProfileId(feedback.getProfileId().toString());
        response.setAuthorId(feedback.getAuthorId().toString());
        response.setAuthorName(author.getName());
        response.setContent(feedback.getContent());
        response.setCreatedAt(feedback.getCreatedAt());
        response.setIsPolished(feedback.getIsPolished());

        return response;
    }
}

