import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { FeedbackComponent } from './features/feedback-component/feedback-component';
import { AgentComponent } from './features/agent-component/agent-component';

const routes: Routes = [
  {
        path: 'feedback',
        component: FeedbackComponent
    },
    {
        path: 'agent',
        component: AgentComponent
    },
    {
        path: '',
        redirectTo: 'feedback',
        pathMatch: 'full'
    }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
