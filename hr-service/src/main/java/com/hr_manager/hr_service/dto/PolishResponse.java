package com.hr_manager.hr_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolishResponse {
    private String originalText;
    private String polishedText;
    private String model;
}

