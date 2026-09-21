package com.franrl.dashboard_service.service;

import com.franrl.dashboard_service.dto.FeedbackResponse;
import com.franrl.dashboard_service.repository.FeedbackRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

}
