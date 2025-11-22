package com.hr_manager.hr_service.repository;

import com.hr_manager.hr_service.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, UUID> {
    List<Feedback> findByProfileIdOrderByCreatedAtDesc(UUID profileId);
}

