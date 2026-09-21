import { Channel } from "../enums/Channel";
import { FeedbackStatus } from "../enums/FeedbackStatus";
import { Sentiment } from "../enums/Sentiment";
import { UrgencyLevel } from "../enums/UrgencyLevel";

export interface FeedbackSubmissionResponse {
  feedbackId: string;
}