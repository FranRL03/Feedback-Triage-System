import { Injectable, Service } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environment/environment';

@Injectable({ providedIn: 'root' })
export class WebSocketService {

    connectToFeedback(feedbackId: string): Observable<any> {
        return new Observable(observer => {
            const socket = new WebSocket(`${environment.wsUrl}/ws/feedback/${feedbackId}`);

            socket.onmessage = (event) => {
                observer.next(JSON.parse(event.data))
            };

            socket.onerror = (error) => {
                observer.error(error);
            };

            socket.onclose = () => {
                observer.complete();
            };

            return () => socket.close();
        });

    }

    connectToDashboard(): Observable<any> {
        return new Observable(observer => {
            const socket = new WebSocket(`${environment.wsUrl}/ws/dashboard`);

            socket.onmessage = (event) => {
                observer.next(JSON.parse(event.data));
            };  

            socket.onerror = (error) => observer.error(error);
            socket.onclose = () => observer.complete();

            return () => socket.close();
        });

    }
}
