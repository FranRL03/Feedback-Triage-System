import { Channel } from "../enums/Channel";
import { FeedbackStatus } from "../enums/FeedbackStatus";
import { Sentiment } from "../enums/Sentiment";
import { UrgencyLevel } from "../enums/UrgencyLevel";

export interface FeedbackResponse {
  content:          Content[];
  empty:            boolean;
  first:            boolean;
  last:             boolean;
  number:           number;
  numberOfElements: number;
  pageable:         Pageable;
  size:             number;
  sort:             Sort;
  totalElements:    number;
  totalPages:       number;
}

export interface Content {
  feedbackId: string;
  message: string;
  emailContact: string;
  channel: Channel;
  urgency: UrgencyLevel;
  sentiment: Sentiment;
  category: string;
  needsReview: boolean;
  status: FeedbackStatus;
  classifiedAt: string;
}

export interface Pageable {
  offset:     number;
  pageNumber: number;
  pageSize:   number;
  paged:      boolean;
  sort:       Sort;
  unpaged:    boolean;
}

export interface Sort {
  empty:    boolean;
  sorted:   boolean;
  unsorted: boolean;
}
