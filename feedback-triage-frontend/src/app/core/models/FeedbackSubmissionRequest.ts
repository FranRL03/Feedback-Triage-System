import { Channel } from "../enums/Channel"

export interface FeedbackSubmissionRequest {
    message: string
    emailContact: string
    channel: Channel
} 