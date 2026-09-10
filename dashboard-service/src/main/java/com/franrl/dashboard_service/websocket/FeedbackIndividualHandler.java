package com.franrl.dashboard_service.websocket;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FeedbackIndividualHandler extends TextWebSocketHandler {

    private final Map<UUID, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        UUID feedbackId = extractFeedbackId(session);

        sessions.put(feedbackId, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        UUID feedbackId = extractFeedbackId(session);

        sessions.remove(feedbackId);
    }

    public void sendToFeedback(UUID feedbackId, String message) {
        WebSocketSession session = sessions.get(feedbackId);

        if (session == null || !session.isOpen()) {
            return;
        }

        try {
            session.sendMessage(new TextMessage(message));
        } catch (IOException e) {
            sessions.remove(feedbackId);
        }

    }

    private UUID extractFeedbackId(WebSocketSession session) {
        String path = session.getUri().getPath();
        String feedbackIdStr = path.substring(path.lastIndexOf("/") + 1);
        UUID feedbackId = UUID.fromString(feedbackIdStr);

        return feedbackId;
    }
}
