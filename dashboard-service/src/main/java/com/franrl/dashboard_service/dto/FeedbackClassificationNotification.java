package com.franrl.dashboard_service.dto;

import com.franrl.enums.Sentiment;
import com.franrl.enums.UrgencyLevel;

import java.util.UUID;

public record FeedbackClassificationNotification(
        UUID feedbackId,
        UrgencyLevel urgency,
        Sentiment sentiment,
        String category,
        boolean needsReview
) {
}
