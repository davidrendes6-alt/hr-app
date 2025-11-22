package com.hr_manager.hr_service.service;

import com.hr_manager.hr_service.dto.ProfileResponse;
import com.hr_manager.hr_service.dto.PublicProfileResponse;
import com.hr_manager.hr_service.dto.UpdateProfileRequest;
import com.hr_manager.hr_service.entity.User;
import com.hr_manager.hr_service.exception.ForbiddenException;
import com.hr_manager.hr_service.exception.ResourceNotFoundException;
import com.hr_manager.hr_service.repository.UserRepository;
import com.hr_manager.hr_service.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getMyProfile(UserPrincipal principal) {
        User user = userRepository.findById(principal.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return mapToFullProfile(user);
    }

    @Transactional(readOnly = true)
    public Object getProfileById(UUID profileId, UserPrincipal principal) {
        User user = userRepository.findById(profileId)
            .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        // Manager or profile owner can see full profile
        if (principal.isManager() || principal.getUserId().equals(profileId)) {
            return mapToFullProfile(user);
        }

        // Other employees see public profile only
        return mapToPublicProfile(user);
    }

    @Transactional
    public ProfileResponse updateProfile(UUID profileId, UpdateProfileRequest request, UserPrincipal principal) {
        // Only owner or manager can update
        if (!principal.isManager() && !principal.getUserId().equals(profileId)) {
            throw new ForbiddenException("Not authorized to update this profile");
        }

        User user = userRepository.findById(profileId)
            .orElseThrow(() -> new ResourceNotFoundException("Profile not found"));

        // Update fields if provided
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
        }
        if (request.getPosition() != null) {
            user.setPosition(request.getPosition());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress());
        }
        if (request.getEmergencyContact() != null) {
            user.setEmergencyContact(request.getEmergencyContact());
        }
        if (request.getSalary() != null) {
            user.setSalary(request.getSalary());
        }

        User updatedUser = userRepository.save(user);

        return mapToFullProfile(updatedUser);
    }

    @Transactional(readOnly = true)
    public List<PublicProfileResponse> getAllProfiles() {
        return userRepository.findAll().stream()
            .map(this::mapToPublicProfile)
            .collect(Collectors.toList());
    }

    private ProfileResponse mapToFullProfile(User user) {
        ProfileResponse response = new ProfileResponse();
        response.setId(user.getId().toString());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setDepartment(user.getDepartment());
        response.setPosition(user.getPosition());
        response.setHireDate(user.getHireDate());
        response.setSalary(user.getSalary());
        response.setPhoneNumber(user.getPhoneNumber());
        response.setAddress(user.getAddress());
        response.setEmergencyContact(user.getEmergencyContact());
        response.setBankAccount(user.getBankAccount());
        response.setSsn(user.getSsn());
        return response;
    }

    private PublicProfileResponse mapToPublicProfile(User user) {
        PublicProfileResponse response = new PublicProfileResponse();
        response.setId(user.getId().toString());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setDepartment(user.getDepartment());
        response.setPosition(user.getPosition());
        return response;
    }
}

