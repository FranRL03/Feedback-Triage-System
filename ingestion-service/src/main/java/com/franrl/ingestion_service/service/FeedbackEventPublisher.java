package com.franrl.ingestion_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.franrl.events.FeedbackRawEvent;
import com.franrl.KafkaTopics;

@Component
public class FeedbackEventPublisher {

    private final KafkaTemplate<String, FeedbackRawEvent> kafkaTemplate;


    public FeedbackEventPublisher(KafkaTemplate<String, FeedbackRawEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(FeedbackRawEvent event) {
        kafkaTemplate.send(KafkaTopics.FEEDBACK_RAW, event.feedbackId().toString(), event);
    }
}
