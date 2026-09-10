package com.franrl.ingestion_service.controller;

import com.franrl.ingestion_service.dto.FeedbackSubmissionRequest;
import com.franrl.ingestion_service.dto.FeedbackSubmissionResponse;
import com.franrl.ingestion_service.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/feedback")
public class FeedBackController {

    private final FeedbackService service;

    public FeedBackController(FeedbackService service) {
        this.service = service;
    }

    @PostMapping("/")
    public ResponseEntity<FeedbackSubmissionResponse> feedbackSubmission(@Valid  @RequestBody FeedbackSubmissionRequest feedbackSubmissionRequest) {

        UUID feedbackId = service.submitFeedback(feedbackSubmissionRequest);
        FeedbackSubmissionResponse result = new FeedbackSubmissionResponse(feedbackId);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(result);
    }
}
