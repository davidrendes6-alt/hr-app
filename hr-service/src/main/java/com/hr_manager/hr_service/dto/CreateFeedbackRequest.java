package com.hr_manager.hr_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateFeedbackRequest {
    @NotBlank(message = "Content is required")
    private String content;

    private Boolean polishWithAI = false;
}

