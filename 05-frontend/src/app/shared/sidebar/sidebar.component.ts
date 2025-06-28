import { Component, EventEmitter, Output } from '@angular/core';

interface MenuItem {
  label: string;
  icon: string;
  route: string;
  roles: string[];
}

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent {
  @Output() closeSidebar = new EventEmitter<void>();

  menuItems: MenuItem[] = [
    {
      label: 'Dashboard',
      icon: 'dashboard',
      route: '/dashboard',
      roles: ['ADMIN', 'DOCTOR', 'PATIENT']
    },
    {
      label: 'Appointments',
      icon: 'event',
      route: '/appointments',
      roles: ['ADMIN', 'DOCTOR', 'PATIENT']
    },
    {
      label: 'Medical Records',
      icon: 'medical_services',
      route: '/emr',
      roles: ['ADMIN', 'DOCTOR', 'PATIENT']
    },
    {
      label: 'Users',
      icon: 'people',
      route: '/users',
      roles: ['ADMIN']
    },
    {
      label: 'Notifications',
      icon: 'notifications',
      route: '/notifications',
      roles: ['ADMIN', 'DOCTOR', 'PATIENT']
    },
    {
      label: 'Doctor Availability',
      icon: 'schedule',
      route: '/availability',
      roles: ['ADMIN', 'DOCTOR']
    }
  ];

  onCloseSidebar() {
    this.closeSidebar.emit();
  }
} 