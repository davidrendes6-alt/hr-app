package com.hr_manager.ai_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolishRequest {

    @NotBlank(message = "Text is required")
    private String text;

    private String context;
}

