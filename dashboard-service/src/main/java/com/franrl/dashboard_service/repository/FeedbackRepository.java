package com.franrl.dashboard_service.repository;

import com.franrl.dashboard_service.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, UUID> {
}
