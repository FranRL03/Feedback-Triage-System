package com.franrl.ai_classifier_service.ai;

import com.franrl.ai_classifier_service.dto.ClassificationResult;
import com.franrl.enums.Channel;
import com.franrl.enums.Sentiment;
import com.franrl.enums.UrgencyLevel;
import com.franrl.events.FeedbackClassifiedEvent;
import com.franrl.events.FeedbackRawEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.UUID;

@SpringBootTest
public class FeedbackClassifierAIClientTest {

    @Autowired
    private FeedbackClassifierAIClient client;

    @Test
    void classifyUrgentFeedback() {
        FeedbackRawEvent event = new FeedbackRawEvent(
                UUID.randomUUID(),
                "Llevo 3 días sin poder iniciar sesión, es urgente",
                "fran@example.com",
                Channel.WEB,
                Instant.now()
        );

        ClassificationResult result = client.classify(event);

        System.out.println(result);
    }
}
