import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { FeedbackSubmissionRequest } from '../core/models/FeedbackSubmissionRequest';
import { Observable } from 'rxjs';
import { FeedbackSubmissionResponse } from '../core/models/FeedbackSubmissionResponse';
import { environment } from '../environment/environment';
import { FeedbackResponse } from '../core/models/FeedbackResponse';
import { FeedbackStatus } from '../core/enums/FeedbackStatus';
import { UrgencyLevel } from '../core/enums/UrgencyLevel';

@Injectable({ providedIn: 'root' })
export class FeedbackApiService {

    constructor(private http: HttpClient) { }

    submitFeedback(request: FeedbackSubmissionRequest): Observable<FeedbackSubmissionResponse> {
        return this.http.post<FeedbackSubmissionResponse>(environment.ingestionUrl, request);
    }

    /*getFeedbacks(page: number): Observable<FeedbackResponse> {
        return this.http.get<FeedbackResponse>(
            `${environment.dashboardUrl}?page=${page}`
        );
    } */

    getFeedbacks(page: number, urgency?: UrgencyLevel, status?: FeedbackStatus): Observable<FeedbackResponse> {
        let params = new HttpParams().set('page', page.toString());

        if (urgency) params = params.set('urgency', urgency);
        if (status) params = params.set('status', status);

        return this.http.get<FeedbackResponse>(environment.dashboardUrl, { params });
    }
}
