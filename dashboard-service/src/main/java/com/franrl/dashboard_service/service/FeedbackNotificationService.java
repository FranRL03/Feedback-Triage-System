package com.franrl.dashboard_service.service;

import com.franrl.dashboard_service.dto.FeedbackClassificationNotification;
import com.franrl.dashboard_service.dto.FeedbackStatusNotification;
import com.franrl.dashboard_service.entity.FeedbackStatus;
import com.franrl.dashboard_service.websocket.DashboardBroadcastHandler;
import com.franrl.dashboard_service.websocket.FeedbackIndividualHandler;
import com.franrl.events.FeedbackClassifiedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Service
@Slf4j
public class FeedbackNotificationService {

    private final JsonMapper mapper;
    private final DashboardBroadcastHandler dashboardBroadcastHandler;
    private final FeedbackIndividualHandler feedbackIndividualHandler;

    public FeedbackNotificationService(
            JsonMapper jsonMapper,
            DashboardBroadcastHandler broadcastHandler,
            FeedbackIndividualHandler feedbackIndividual
    ) {
        this.mapper = jsonMapper;
        this.dashboardBroadcastHandler = broadcastHandler;
        this.feedbackIndividualHandler = feedbackIndividual;
    }

    public void notifyClassification(FeedbackClassifiedEvent event) {
        FeedbackClassificationNotification notification =
                new FeedbackClassificationNotification(
                        event.feedbackId(),
                        event.urgency(),
                        event.sentiment(),
                        event.category(),
                        event.needsReview()
                );

        try {
            String json = mapper.writeValueAsString(notification);
            dashboardBroadcastHandler.broadcast(json);

            FeedbackStatusNotification statusNotification = new FeedbackStatusNotification(FeedbackStatus.CLASSIFIED);
            String statusJson = mapper.writeValueAsString(statusNotification);
            feedbackIndividualHandler.sendToFeedback(event.feedbackId(), statusJson);
        } catch (JacksonException e) {
            log.error("Error serializing notification for feedback {}", event.feedbackId(), e);
        }
    }
}