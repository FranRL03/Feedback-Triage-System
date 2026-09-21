package com.franrl.dashboard_service.controller;

import com.franrl.dashboard_service.dto.FeedbackResponse;
import com.franrl.dashboard_service.service.FeedBackService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/feedback")
public class FeedbackQueryController {

    private final FeedBackService feedbackService;

    public FeedbackQueryController(FeedBackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping("/")
    public Page<FeedbackResponse> getFeedbacks (@PageableDefault(page=0, size =4)Pageable pageable) {
        return feedbackService.getFeedbacks(pageable);
    }
}
