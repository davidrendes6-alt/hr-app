package com.hr_manager.hr_service.service;

import com.hr_manager.hr_service.dto.AbsenceRequestResponse;
import com.hr_manager.hr_service.dto.CreateAbsenceRequestRequest;
import com.hr_manager.hr_service.entity.AbsenceRequest;
import com.hr_manager.hr_service.entity.User;
import com.hr_manager.hr_service.exception.BadRequestException;
import com.hr_manager.hr_service.exception.ForbiddenException;
import com.hr_manager.hr_service.exception.ResourceNotFoundException;
import com.hr_manager.hr_service.repository.AbsenceRequestRepository;
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
public class AbsenceRequestService {

    private final AbsenceRequestRepository absenceRequestRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<AbsenceRequestResponse> getMyAbsenceRequests(UserPrincipal principal) {
        List<AbsenceRequest> requests = absenceRequestRepository
            .findByEmployeeIdOrderByCreatedAtDesc(principal.getUserId());

        return requests.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AbsenceRequestResponse> getPendingAbsenceRequests(UserPrincipal principal) {
        if (!principal.isManager()) {
            throw new ForbiddenException("Not authorized - manager role required");
        }

        List<AbsenceRequest> requests = absenceRequestRepository
            .findByStatusOrderByCreatedAtAsc("pending");

        return requests.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public AbsenceRequestResponse createAbsenceRequest(CreateAbsenceRequestRequest request, UserPrincipal principal) {
        // Validate dates
        if (request.getEndDate().isBefore(request.getStartDate())) {
            throw new BadRequestException("End date cannot be before start date");
        }

        AbsenceRequest absenceRequest = new AbsenceRequest();
        absenceRequest.setEmployeeId(principal.getUserId());
        absenceRequest.setStartDate(request.getStartDate());
        absenceRequest.setEndDate(request.getEndDate());
        absenceRequest.setReason(request.getReason());
        absenceRequest.setStatus("pending");

        AbsenceRequest saved = absenceRequestRepository.save(absenceRequest);

        return mapToResponse(saved);
    }

    @Transactional
    public void approveAbsenceRequest(UUID requestId, UserPrincipal principal) {
        if (!principal.isManager()) {
            throw new ForbiddenException("Not authorized - manager role required");
        }

        AbsenceRequest request = absenceRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Absence request not found"));

        request.setStatus("approved");
        absenceRequestRepository.save(request);
    }

    @Transactional
    public void rejectAbsenceRequest(UUID requestId, UserPrincipal principal) {
        if (!principal.isManager()) {
            throw new ForbiddenException("Not authorized - manager role required");
        }

        AbsenceRequest request = absenceRequestRepository.findById(requestId)
            .orElseThrow(() -> new ResourceNotFoundException("Absence request not found"));

        request.setStatus("rejected");
        absenceRequestRepository.save(request);
    }

    private AbsenceRequestResponse mapToResponse(AbsenceRequest request) {
        User employee = userRepository.findById(request.getEmployeeId())
            .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        AbsenceRequestResponse response = new AbsenceRequestResponse();
        response.setId(request.getId().toString());
        response.setEmployeeId(request.getEmployeeId().toString());
        response.setEmployeeName(employee.getName());
        response.setStartDate(request.getStartDate());
        response.setEndDate(request.getEndDate());
        response.setReason(request.getReason());
        response.setStatus(request.getStatus());
        response.setCreatedAt(request.getCreatedAt());

        return response;
    }
}

