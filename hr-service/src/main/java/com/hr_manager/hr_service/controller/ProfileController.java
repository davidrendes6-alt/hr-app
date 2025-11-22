package com.hr_manager.hr_service.controller;

import com.hr_manager.hr_service.dto.ProfileResponse;
import com.hr_manager.hr_service.dto.PublicProfileResponse;
import com.hr_manager.hr_service.dto.UpdateProfileRequest;
import com.hr_manager.hr_service.security.UserPrincipal;
import com.hr_manager.hr_service.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserPrincipal principal) {
        ProfileResponse profile = profileService.getMyProfile(principal);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProfileById(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID profileId = UUID.fromString(id);
        Object profile = profileService.getProfileById(profileId, principal);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponse> updateProfile(
            @PathVariable String id,
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID profileId = UUID.fromString(id);
        ProfileResponse profile = profileService.updateProfile(profileId, request, principal);
        return ResponseEntity.ok(profile);
    }

    @GetMapping
    public ResponseEntity<List<PublicProfileResponse>> getAllProfiles() {
        List<PublicProfileResponse> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }
}

