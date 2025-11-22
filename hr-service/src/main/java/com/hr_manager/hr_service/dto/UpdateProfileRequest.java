package com.hr_manager.hr_service.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String name;

    @Email
    private String email;

    private String department;
    private String position;
    private String phoneNumber;
    private String address;
    private String emergencyContact;
    private BigDecimal salary;
}

