package com.franrl.dashboard_service.dto;

import com.franrl.dashboard_service.entity.FeedbackEntity;
import com.franrl.dashboard_service.entity.FeedbackStatus;
import com.franrl.enums.Channel;
import com.franrl.enums.Sentiment;
import com.franrl.enums.UrgencyLevel;

import java.time.Instant;
import java.util.UUID;

public record FeedbackResponse(
        UUID feedbackId,
        String message,
        String emailContact,
        Channel channel,
        UrgencyLevel urgency,
        Sentiment sentiment,
        String category,
        Boolean needsReview,
        FeedbackStatus status,
        Instant classifiedAt
) {

    public static FeedbackResponse fromEntity(FeedbackEntity entity) {
        return new FeedbackResponse(
                entity.getFeedbackId(),
                entity.getMessage(),
                entity.getEmailContact(),
                entity.getChannel(),
                entity.getUrgency(),
                entity.getSentiment(),
                entity.getCategory(),
                entity.getNeedsReview(),
                entity.getStatus(),
                entity.getClassifiedAt()
        );
    }
}
