package com.franrl.dashboard_service.service;

import com.franrl.dashboard_service.entity.FeedbackEntity;
import com.franrl.dashboard_service.entity.FeedbackStatus;
import com.franrl.enums.UrgencyLevel;
import org.springframework.data.jpa.domain.Specification;

public class FeedbackSpecifications {

    public static Specification<FeedbackEntity> hasUrgency(UrgencyLevel urgency) {
        return (root, query, cb) ->
                urgency == null ? null : cb.equal(root.get("urgency"), urgency);
    }

    public static Specification<FeedbackEntity> hasStatus(FeedbackStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }
}
