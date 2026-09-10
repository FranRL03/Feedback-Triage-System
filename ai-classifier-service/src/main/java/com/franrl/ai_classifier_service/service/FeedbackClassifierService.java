package com.franrl.ai_classifier_service.service;

import com.franrl.ai_classifier_service.ai.FeedbackClassifierAIClient;
import com.franrl.ai_classifier_service.dto.ClassificationResult;
import com.franrl.events.FeedbackClassifiedEvent;
import com.franrl.events.FeedbackRawEvent;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class FeedbackClassifierService {

    private final FeedbackClassifierAIClient feedbackClassifierAIClient;

    public FeedbackClassifierService(FeedbackClassifierAIClient feedbackClassifierAIClient) {
        this.feedbackClassifierAIClient = feedbackClassifierAIClient;
    }

    public FeedbackClassifiedEvent classify(FeedbackRawEvent feedbackRawEvent) {

        ClassificationResult result = feedbackClassifierAIClient.classify(feedbackRawEvent);

        return new FeedbackClassifiedEvent(
                feedbackRawEvent.feedbackId(),
                result.urgency(),
                result.sentiment(),
                result.category(),
                result.needsReview(),
                Instant.now()
        );

    }

}
