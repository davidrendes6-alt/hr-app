package com.hr_manager.hr_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackResponse {
    private String id;
    private String profileId;
    private String authorId;
    private String authorName;
    private String content;
    private LocalDateTime createdAt;
    private Boolean isPolished;
}

