import { Component, Input } from '@angular/core';
import { Content } from '../../core/models/FeedbackResponse';
import { URGENCY_CLASSES, SENTIMENT_CLASSES, STATUS_CLASSES } from '../utils/types-card';

@Component({
  selector: 'app-card-component',
  standalone: false,
  styleUrl: './card-component.css',
  templateUrl: './card-component.html',
})
export class CardComponent {

  @Input() ticket!: Content;

  urgencyClasses = URGENCY_CLASSES;
  sentimentClasses = SENTIMENT_CLASSES;
  statusClasses = STATUS_CLASSES
}
