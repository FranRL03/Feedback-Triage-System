package com.franrl.ingestion_service.service;

import com.franrl.enums.Channel;
import com.franrl.events.FeedbackRawEvent;
import com.franrl.ingestion_service.dto.FeedbackSubmissionRequest;
import com.franrl.ingestion_service.dto.FeedbackSubmissionResponse;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class FeedbackService {

    private final FeedbackEventPublisher publisher;


    public FeedbackService(FeedbackEventPublisher publisher) {
        this.publisher = publisher;
    }

    public UUID submitFeedback (FeedbackSubmissionRequest content) {
        UUID feedbackId = UUID.randomUUID();
        publisher.publish(new FeedbackRawEvent(feedbackId, content.message(), content.emailContact(), content.channel(), Instant.now()));

        return feedbackId;
    }
}
