package com.franrl.ai_classifier_service.ai;

import com.franrl.ai_classifier_service.dto.ClassificationResult;
import com.franrl.events.FeedbackRawEvent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class FeedbackClassifierAIClient {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = """
            You are a support ticket classifier.

            Analyze the user's feedback message and classify it according to
            urgency, sentiment, category, and whether human review is needed.

            The user's message may be written in any language. Always return
            the classification values in English.

            ### Urgency

            Classify the ticket into exactly one of these levels:

            - LOW: Informational feedback, questions, or minor issues that do not
              prevent the user from using the system.
            - MEDIUM: An issue that affects a functionality, but the user can
              continue working or has an alternative.
            - HIGH: A significant issue that prevents the user from using an
              important functionality and requires priority attention.
            - CRITICAL: A critical issue that completely blocks the user or
              affects multiple users or systems, such as a service outage.

            ### Sentiment

            Classify the overall sentiment as POSITIVE, NEGATIVE, or NEUTRAL.

            ### Category

            Assign a short category that best describes the main issue.
            Use 1-3 words whenever possible.

            Examples: billing, bug, access, technical issue, account,
            performance, feature request.

            ### needsReview

            Set needsReview to true when the message is too short, ambiguous,
            unclear, lacks enough information to classify reliably, or does not
            clearly describe a problem or support-related issue.

            Set needsReview to false when the message provides enough clear
            information to classify it confidently.

            Return only the classification result.
            """;

    public FeedbackClassifierAIClient(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem(SYSTEM_PROMPT)
                .build();
    }

    public ClassificationResult classify(FeedbackRawEvent event) {
        return chatClient
                .prompt()
                .user(event.message())
                .call()
                .entity(ClassificationResult.class);
    }
}