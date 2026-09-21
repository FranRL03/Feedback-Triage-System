import { Channel } from "../../core/enums/Channel";
import { FeedbackStatus } from "../../core/enums/FeedbackStatus";
import { Sentiment } from "../../core/enums/Sentiment";
import { UrgencyLevel } from "../../core/enums/UrgencyLevel";

export const URGENCY_CLASSES: Record<UrgencyLevel, string> = {
  [UrgencyLevel.LOW]: 'badge-low',
  [UrgencyLevel.MEDIUM]: 'badge-medium',
  [UrgencyLevel.HIGH]: 'badge-high',
  [UrgencyLevel.CRITICAL]: 'badge-critical',
};

export const SENTIMENT_CLASSES: Record<Sentiment, string> = {
    [Sentiment.POSITIVE]: 'badge-positive',
    [Sentiment.NEGATIVE]: 'badge-negative',
    [Sentiment.NEUTRAL]: 'badge-neutral',
}

export const STATUS_CLASSES: Record<FeedbackStatus, string> = {
    [FeedbackStatus.CLASSIFIED]: 'badge-classified',
    [FeedbackStatus.RECEIVED]: 'badge-received',
    [FeedbackStatus.FAILED]: 'badge-failed'
}

