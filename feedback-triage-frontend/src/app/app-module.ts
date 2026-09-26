import { NgModule, provideBrowserGlobalErrorListeners } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing-module';
import { App } from './app';
import { HeaderComponent } from './shared/header-component/header-component';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { MatTabsModule } from '@angular/material/tabs';
import { FeedbackComponent } from './features/feedback-component/feedback-component';
import { AgentComponent } from './features/agent-component/agent-component';
import { CardComponent } from './shared/card-component/card-component';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { provideHttpClient } from '@angular/common/http';
import { ConfirmedComponent } from './shared/confirmed-component/confirmed-component';

@NgModule({
  declarations: [
    App,
    HeaderComponent,
    FeedbackComponent,
    AgentComponent,
    CardComponent,
    ConfirmedComponent,
  ],
  imports: [BrowserModule, AppRoutingModule, NgbModule, MatTabsModule, MatIconModule, FormsModule],
  providers: [provideBrowserGlobalErrorListeners(), provideHttpClient()],
  bootstrap: [App],
})
export class AppModule {}
