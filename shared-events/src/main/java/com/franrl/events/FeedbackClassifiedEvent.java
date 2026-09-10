package com.franrl.events;

import com.franrl.enums.Sentiment;
import com.franrl.enums.UrgencyLevel;

import java.time.Instant;
import java.util.UUID;

public record FeedbackClassifiedEvent(

        UUID feedbackId,
        UrgencyLevel urgency,
        Sentiment sentiment,
        String category,
        boolean needsReview,
        Instant classifiedAt
) {
}
