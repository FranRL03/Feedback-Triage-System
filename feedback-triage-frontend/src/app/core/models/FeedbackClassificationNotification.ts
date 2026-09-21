import { Sentiment } from '../enums/Sentiment';
import { UrgencyLevel } from '../enums/UrgencyLevel';

export interface FeedbackClassificationNotification {
  feedbackId: string;
  urgency: UrgencyLevel;
  sentiment: Sentiment;
  category: string;
  needsReview: boolean;
}