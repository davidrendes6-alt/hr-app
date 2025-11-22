package com.hr_manager.hr_service.repository;

import com.hr_manager.hr_service.entity.AbsenceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AbsenceRequestRepository extends JpaRepository<AbsenceRequest, UUID> {
    List<AbsenceRequest> findByEmployeeIdOrderByCreatedAtDesc(UUID employeeId);
    List<AbsenceRequest> findByStatusOrderByCreatedAtAsc(String status);
}

