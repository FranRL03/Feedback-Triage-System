package com.franrl.events;

import com.franrl.enums.Channel;

import java.time.Instant;
import java.util.UUID;

public record FeedbackRawEvent (

        UUID feedbackId,
        String message,
        String emailContact,
        Channel channel,
        Instant creationDate
) {

}
