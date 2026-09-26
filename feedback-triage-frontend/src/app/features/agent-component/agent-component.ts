import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Content } from '../../core/models/FeedbackResponse';
import { FeedbackApiService } from '../../service/feedback-api-service';
import { WebSocketService } from '../../service/websocket';
import { FeedbackClassificationNotification } from '../../core/models/FeedbackClassificationNotification';
import { UrgencyLevel } from '../../core/enums/UrgencyLevel';
import { FeedbackStatus } from '../../core/enums/FeedbackStatus';

@Component({
  selector: 'app-agent-component',
  standalone: false,
  styleUrl: './agent-component.css',
  templateUrl: './agent-component.html',
})
export class AgentComponent implements OnInit {

  protected readonly UrgencyLevel = UrgencyLevel;
  protected readonly FeedbackStatus = FeedbackStatus;

  tickets: Content[] = [];
  pageSize = 0;
  numberOfElements = 0;
  totalElements: number = 0;
  pageNumber: number = 1;

  needReviews: number = 0;

  selectedUrgency?: UrgencyLevel;
  selectedStatus?: FeedbackStatus;

  constructor(
    private service: FeedbackApiService,
    private cdr: ChangeDetectorRef,
    private wsService: WebSocketService
  ) { }

  ngOnInit() {
    this.loadFeedback();
    this.listenForNewClassifications();
  }

  loadFeedback(): void {
    this.service.getFeedbacks(this.pageNumber - 1, this.selectedUrgency, this.selectedStatus).subscribe({
      next: response => {
        this.tickets = response.content;
        this.pageSize = response.size;
        this.totalElements = response.totalElements;
        this.numberOfElements = response.numberOfElements;
        this.needReviews = response.content.filter(
          t => t.needsReview).length;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar los feedbacks:', err);
      }
    });
  }

  onFilterChange(): void {
    this.pageNumber = 1;
    this.loadFeedback();
  }

  private listenForNewClassifications(): void {
    this.wsService.connectToDashboard().subscribe({
      next: (notification: FeedbackClassificationNotification) => {
        const existingIndex = this.tickets.findIndex(t => t.feedbackId === notification.feedbackId);

        if (existingIndex !== -1) {
          this.tickets[existingIndex] = {
            ...this.tickets[existingIndex],
            ...notification,
            status: 'CLASSIFIED' as any
          };
          this.cdr.detectChanges();
        } else {
          this.loadFeedback();
        }
      },
      error: (err) => console.error('Error en WebSocket dashboard', err)
    });
  }
}