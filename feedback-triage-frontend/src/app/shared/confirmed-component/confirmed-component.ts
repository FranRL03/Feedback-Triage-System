import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FeedbackStatus } from '../../core/enums/FeedbackStatus';

@Component({
  selector: 'app-confirmed-component',
  standalone: false,
  styleUrl: './confirmed-component.css',
  templateUrl: './confirmed-component.html',
})
export class ConfirmedComponent {
  @Input() status: FeedbackStatus = FeedbackStatus.RECEIVED;
  @Output() reset = new EventEmitter<void>();

  protected readonly FeedbackStatus = FeedbackStatus;

  get isClassified(): boolean {
    return this.status === FeedbackStatus.CLASSIFIED;
  }

  onBackToHome(): void {
    this.reset.emit();
  }
}
