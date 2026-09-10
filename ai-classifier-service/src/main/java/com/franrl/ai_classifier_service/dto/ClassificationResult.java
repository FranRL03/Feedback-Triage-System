package com.franrl.ai_classifier_service.dto;

import com.franrl.enums.Sentiment;
import com.franrl.enums.UrgencyLevel;

public record ClassificationResult(
        UrgencyLevel urgency,
        Sentiment sentiment,
        String category,
        boolean needsReview
) {
}
