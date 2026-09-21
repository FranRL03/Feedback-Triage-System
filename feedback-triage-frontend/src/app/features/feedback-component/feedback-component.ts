import { Component } from '@angular/core';
import { Channel } from '../../core/enums/Channel';
import { FeedbackApiService } from '../../service/feedback-api-service';
import { FeedbackSubmissionRequest } from '../../core/models/FeedbackSubmissionRequest';
import { WebSocketService } from '../../service/websocket';
import { FeedbackStatusNotification } from '../../core/models/FeedbackStatusNotification';
import { FeedbackStatus } from '../../core/enums/FeedbackStatus';

@Component({
  selector: 'app-feedback-component',
  standalone: false,
  styleUrl: './feedback-component.css',
  templateUrl: './feedback-component.html',
})
export class FeedbackComponent {

  protected readonly Channel = Channel;

  message = '';
  emailContact = '';
  channel: Channel = Channel.WEB;

  submitted = false;
  feedbackId: string | null = null;
  errorMessage: string | null = null;

  status: FeedbackStatus = FeedbackStatus.RECEIVED;

  constructor(private service: FeedbackApiService, private wsService: WebSocketService) { }

  onSubmit(): void {
    const request: FeedbackSubmissionRequest = {
      message: this.message,
      emailContact: this.emailContact,
      channel: this.channel
    };

    this.service.submitFeedback(request).subscribe({
      next: (response) => {
        this.feedbackId = response.feedbackId;
        this.submitted = true;
        this.status = FeedbackStatus.RECEIVED;
        this.listenForClassification(response.feedbackId);
      },
      error: (err) => {
        console.error('Error enviando feedback', err);
        this.errorMessage = 'No se pudo enviar tu feedback. Inténtalo de nuevo.';
      }
    });
  }

  private listenForClassification(feedbackId: string): void {
    this.wsService.connectToFeedback(feedbackId).subscribe({
      next: (notification: FeedbackStatusNotification) => {
        this.status = notification.status;
      },
      error: (err) => console.error('Error en WebSocket', err)
    });
  }
}
