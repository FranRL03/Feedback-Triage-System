package com.franrl.dashboard_service.config;

import com.franrl.dashboard_service.websocket.DashboardBroadcastHandler;
import com.franrl.dashboard_service.websocket.FeedbackIndividualHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final DashboardBroadcastHandler dashboardBroadcastHandler;
    private final FeedbackIndividualHandler feedbackIndividualHandler;

    public WebSocketConfig(DashboardBroadcastHandler dashboardBroadcastHandler, FeedbackIndividualHandler feedbackIndividualHandler) {
        this.dashboardBroadcastHandler = dashboardBroadcastHandler;
        this.feedbackIndividualHandler = feedbackIndividualHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(dashboardBroadcastHandler, "/ws/dashboard")
                .setAllowedOrigins("http://localhost:4200");

        registry.addHandler(feedbackIndividualHandler, "/ws/feedback/{feedbackId}")
                .setAllowedOrigins("http://localhost:4200");
    }
}
