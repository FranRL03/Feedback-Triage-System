package com.franrl.dashboard_service.service;

import com.franrl.dashboard_service.dto.FeedbackResponse;
import com.franrl.dashboard_service.entity.FeedbackEntity;
import com.franrl.dashboard_service.entity.FeedbackStatus;
import com.franrl.dashboard_service.repository.FeedbackRepository;
import com.franrl.dashboard_service.repository.FeedbackSpecifications;
import com.franrl.enums.UrgencyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class FeedBackService {

    private final FeedbackRepository feedbackRepository;

    public FeedBackService(final FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public Page<FeedbackResponse> getFeedbacks (Pageable pageable) {
        return feedbackRepository.findAllByOrderByCreationDateDesc(pageable)
                .map(FeedbackResponse::fromEntity);
    }

    public Page<FeedbackResponse> getFeedbacks(UrgencyLevel urgency, FeedbackStatus status, Pageable pageable) {
        Specification<FeedbackEntity> spec = Specification
                .where(FeedbackSpecifications.hasUrgency(urgency))
                .and(FeedbackSpecifications.hasStatus(status));

        return feedbackRepository.findAll(spec, pageable)
                .map(FeedbackResponse::fromEntity);
    }

}
