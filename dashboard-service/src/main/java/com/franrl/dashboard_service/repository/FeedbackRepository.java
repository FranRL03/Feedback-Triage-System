package com.franrl.dashboard_service.repository;

import com.franrl.dashboard_service.entity.FeedbackEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, UUID>,
        JpaSpecificationExecutor<FeedbackEntity> {

    Page<FeedbackEntity> findAllByOrderByCreationDateDesc(Pageable pageable);
}
