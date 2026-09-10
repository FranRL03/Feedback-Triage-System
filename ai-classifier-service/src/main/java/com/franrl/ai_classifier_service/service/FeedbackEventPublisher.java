package com.franrl.ai_classifier_service.service;

import com.franrl.KafkaTopics;
import com.franrl.events.FeedbackClassifiedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class FeedbackEventPublisher {

    private final KafkaTemplate<String, FeedbackClassifiedEvent> kafkaTemplate;


    public FeedbackEventPublisher(KafkaTemplate<String, FeedbackClassifiedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(FeedbackClassifiedEvent event) {
        kafkaTemplate.send(KafkaTopics.FEEDBACK_CLASSIFIED, event.feedbackId().toString(), event);
    }
}
