package com.franrl.dashboard_service.event;

import com.franrl.KafkaTopics;
import com.franrl.dashboard_service.entity.FeedbackEntity;
import com.franrl.dashboard_service.entity.FeedbackStatus;
import com.franrl.dashboard_service.repository.FeedbackRepository;
import com.franrl.dashboard_service.service.FeedbackNotificationService;
import com.franrl.events.FeedbackClassifiedEvent;
import com.franrl.events.FeedbackRawEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
@Slf4j
public class FeedbackEventListener {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackNotificationService feedbackNotificationService;

    @KafkaListener(topics = KafkaTopics.FEEDBACK_RAW, groupId = "dashboard-group")
    public void onFeedbackRaw(FeedbackRawEvent event) {

        FeedbackEntity feedbackEntity = FeedbackEntity.builder()
                .feedbackId(event.feedbackId())
                .message(event.message())
                .emailContact(event.emailContact())
                .channel(event.channel())
                .creationDate(event.creationDate())
                .status(FeedbackStatus.RECEIVED)
                .build();

        feedbackRepository.save(feedbackEntity);
    }

    @KafkaListener(topics = KafkaTopics.FEEDBACK_CLASSIFIED, groupId = "dashboard-group")
    public void onFeedbackClassified(FeedbackClassifiedEvent event) {

        Optional<FeedbackEntity> optionalEntity = feedbackRepository.findById(event.feedbackId());

        if (optionalEntity.isEmpty()) {
            log.warn("Feedback not found for classified event: {}", event.feedbackId());
            return;
        }

        FeedbackEntity feedbackEntity = optionalEntity.get();
        feedbackEntity.setUrgency(event.urgency());
        feedbackEntity.setSentiment(event.sentiment());
        feedbackEntity.setCategory(event.category());
        feedbackEntity.setNeedsReview(event.needsReview());
        feedbackEntity.setClassifiedAt(event.classifiedAt());
        feedbackEntity.setStatus(FeedbackStatus.CLASSIFIED);

        feedbackRepository.save(feedbackEntity);
        feedbackNotificationService.notifyClassification(event);
    }
}
