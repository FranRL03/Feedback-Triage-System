import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FeedbackSubmissionRequest } from '../core/models/FeedbackSubmissionRequest';
import { Observable } from 'rxjs';
import { FeedbackSubmissionResponse } from '../core/models/FeedbackSubmissionResponse';
import { environment } from '../environment/environment';
import { FeedbackResponse } from '../core/models/FeedbackResponse';

@Injectable({ providedIn: 'root' })
export class FeedbackApiService {

    constructor(private http: HttpClient) { }

    submitFeedback(request: FeedbackSubmissionRequest): Observable<FeedbackSubmissionResponse> {
        return this.http.post<FeedbackSubmissionResponse>(environment.ingestionUrl, request);
    }

    getFeedbacks(page: number): Observable<FeedbackResponse> {
        return this.http.get<FeedbackResponse>(
            `${environment.dashboardUrl}?page=${page}`
        );
    }
}
