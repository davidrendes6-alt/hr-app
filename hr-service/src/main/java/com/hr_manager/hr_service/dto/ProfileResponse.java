package com.hr_manager.hr_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileResponse {
    private String id;
    private String name;
    private String email;
    private String role;
    private String department;
    private String position;
    private LocalDate hireDate;
    private BigDecimal salary;
    private String phoneNumber;
    private String address;
    private String emergencyContact;
    private String bankAccount;
    private String ssn;
}

