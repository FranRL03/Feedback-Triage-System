package com.franrl.ingestion_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.franrl.enums.Channel;

public record FeedbackSubmissionRequest(

        @NotBlank(message = "{message.blank}")
        String message,

        @NotBlank(message = "{email.blank}")
        @Email(message = "{email.format}")
        String emailContact,

        @NotNull(message = "{channel.null}")
        Channel channel
) {
}
