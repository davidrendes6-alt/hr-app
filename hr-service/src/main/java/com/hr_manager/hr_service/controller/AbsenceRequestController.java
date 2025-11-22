package com.hr_manager.hr_service.controller;

import com.hr_manager.hr_service.dto.AbsenceRequestResponse;
import com.hr_manager.hr_service.dto.CreateAbsenceRequestRequest;
import com.hr_manager.hr_service.dto.MessageResponse;
import com.hr_manager.hr_service.security.UserPrincipal;
import com.hr_manager.hr_service.service.AbsenceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/absences")
@RequiredArgsConstructor
public class AbsenceRequestController {

    private final AbsenceRequestService absenceRequestService;

    @GetMapping("/me")
    public ResponseEntity<List<AbsenceRequestResponse>> getMyAbsenceRequests(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<AbsenceRequestResponse> requests = absenceRequestService.getMyAbsenceRequests(principal);
        return ResponseEntity.ok(requests);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<AbsenceRequestResponse>> getPendingAbsenceRequests(
            @AuthenticationPrincipal UserPrincipal principal) {
        List<AbsenceRequestResponse> requests = absenceRequestService.getPendingAbsenceRequests(principal);
        return ResponseEntity.ok(requests);
    }

    @PostMapping
    public ResponseEntity<AbsenceRequestResponse> createAbsenceRequest(
            @Valid @RequestBody CreateAbsenceRequestRequest request,
            @AuthenticationPrincipal UserPrincipal principal) {
        AbsenceRequestResponse response = absenceRequestService.createAbsenceRequest(request, principal);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<MessageResponse> approveAbsenceRequest(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID requestId = UUID.fromString(id);
        absenceRequestService.approveAbsenceRequest(requestId, principal);
        return ResponseEntity.ok(new MessageResponse("Absence request approved", id));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<MessageResponse> rejectAbsenceRequest(
            @PathVariable String id,
            @AuthenticationPrincipal UserPrincipal principal) {
        UUID requestId = UUID.fromString(id);
        absenceRequestService.rejectAbsenceRequest(requestId, principal);
        return ResponseEntity.ok(new MessageResponse("Absence request rejected", id));
    }
}

