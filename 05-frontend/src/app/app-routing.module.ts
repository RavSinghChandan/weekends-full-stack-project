import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { RegisterComponent } from './features/auth/register/register.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  // Future: Lazy load feature modules
  // { path: 'dashboard', loadChildren: () => import('./features/dashboard/dashboard.module').then(m => m.DashboardModule) },
  // { path: 'appointments', loadChildren: () => import('./features/appointments/appointments.module').then(m => m.AppointmentsModule) },
  // { path: 'emr', loadChildren: () => import('./features/emr/emr.module').then(m => m.EMRModule) },
  // { path: 'users', loadChildren: () => import('./features/users/users.module').then(m => m.UsersModule) },
  // { path: 'notifications', loadChildren: () => import('./features/notifications/notifications.module').then(m => m.NotificationsModule) },
  // { path: 'availability', loadChildren: () => import('./features/availability/availability.module').then(m => m.AvailabilityModule) },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'enabled' })],
  exports: [RouterModule]
})
export class AppRoutingModule {} 