package com.franrl.ai_classifier_service.service;

import com.franrl.KafkaTopics;
import com.franrl.events.FeedbackClassifiedEvent;
import com.franrl.events.FeedbackRawEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class FeedbackRawEventListener {

    private final FeedbackClassifierService feedbackClassifierService;
    private final FeedbackEventPublisher feedbackEventPublisher;

    public FeedbackRawEventListener(FeedbackClassifierService feedbackClassifierService, FeedbackEventPublisher feedbackEventPublisher) {
        this.feedbackClassifierService = feedbackClassifierService;
        this.feedbackEventPublisher = feedbackEventPublisher;
    }

    @KafkaListener(topics = KafkaTopics.FEEDBACK_RAW, groupId = "ai-classifier-group")
    public void onFeedbackRaw(FeedbackRawEvent feedbackRawEvent) {

        FeedbackClassifiedEvent classifiedEvent = feedbackClassifierService.classify(feedbackRawEvent);
        feedbackEventPublisher.publish(classifiedEvent);

    }
}
