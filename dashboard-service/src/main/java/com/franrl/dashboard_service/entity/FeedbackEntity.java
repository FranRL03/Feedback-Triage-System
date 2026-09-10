package com.franrl.dashboard_service.entity;

import com.franrl.enums.Channel;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import com.franrl.enums.UrgencyLevel;
import com.franrl.enums.Sentiment;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@ToString
@SuperBuilder
@NoArgsConstructor
@Table(name = "feedback")
public class FeedbackEntity {

    @Id
    private UUID feedbackId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    private String emailContact;

    @Enumerated(EnumType.STRING)
    private Channel channel;

    private Instant creationDate;

    @Enumerated(EnumType.STRING)
    private UrgencyLevel urgency;

    @Enumerated(EnumType.STRING)
    private Sentiment sentiment;

    private String category;

    private Boolean needsReview;

    private Instant classifiedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackStatus status;
}
