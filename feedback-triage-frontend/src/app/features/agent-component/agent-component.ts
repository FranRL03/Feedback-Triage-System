import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Content } from '../../core/models/FeedbackResponse';
import { FeedbackApiService } from '../../service/feedback-api-service';
import { WebSocketService } from '../../service/websocket';
import { FeedbackClassificationNotification } from '../../core/models/FeedbackClassificationNotification';

@Component({
  selector: 'app-agent-component',
  standalone: false,
  styleUrl: './agent-component.css',
  templateUrl: './agent-component.html',
})
export class AgentComponent implements OnInit {

  tickets: Content[] = [];
  pageSize = 0;
  numberOfElements = 0;
  totalElements: number = 0;
  pageNumber: number = 1;

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
    this.service.getFeedbacks(this.pageNumber - 1).subscribe({
      next: response => {
        this.tickets = response.content;
        this.pageSize = response.size;
        this.totalElements = response.totalElements;
        this.numberOfElements = response.numberOfElements;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar los feedbacks:', err);
      }
    });
  }

  private listenForNewClassifications(): void {
  this.wsService.connectToDashboard().subscribe({
    next: (notification: FeedbackClassificationNotification) => {
      const existingIndex = this.tickets.findIndex(t => t.feedbackId === notification.feedbackId);

      if (existingIndex !== -1) {
        // Ya estaba en la lista (llegó como RECEIVED antes) → lo actualizamos in-place
        this.tickets[existingIndex] = {
          ...this.tickets[existingIndex],
          ...notification,
          status: 'CLASSIFIED' as any
        };
      } else {
        // Ticket completamente nuevo, no estaba en la carga inicial → recargamos
        this.loadFeedback();
      }
    },
    error: (err) => console.error('Error en WebSocket dashboard', err)
  });
}
}
