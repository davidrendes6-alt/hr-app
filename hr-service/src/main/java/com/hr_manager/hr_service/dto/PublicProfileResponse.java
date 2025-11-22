package com.hr_manager.hr_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicProfileResponse {
    private String id;
    private String name;
    private String email;
    private String department;
    private String position;
}

