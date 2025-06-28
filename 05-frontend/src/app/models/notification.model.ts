import { User } from './user.model';

export interface Notification {
  id: number;
  user: User;
  title: string;
  message: string;
  type: NotificationType;
  priority: NotificationPriority;
  isRead: boolean;
  createdAt: string;
  updatedAt: string;
}

export enum NotificationType {
  APPOINTMENT_REMINDER = 'APPOINTMENT_REMINDER',
  APPOINTMENT_CONFIRMATION = 'APPOINTMENT_CONFIRMATION',
  APPOINTMENT_CANCELLATION = 'APPOINTMENT_CANCELLATION',
  EMR_UPDATE = 'EMR_UPDATE',
  SYSTEM = 'SYSTEM'
}

export enum NotificationPriority {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  URGENT = 'URGENT'
}

export interface NotificationRequest {
  userId: number;
  title: string;
  message: string;
  type: NotificationType;
  priority: NotificationPriority;
}

export interface NotificationResponse {
  id: number;
  user: User;
  title: string;
  message: string;
  type: NotificationType;
  priority: NotificationPriority;
  isRead: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface NotificationStatistics {
  totalNotifications: number;
  unreadNotifications: number;
  highPriorityNotifications: number;
  notificationsThisMonth: number;
  notificationsByType: { [key: string]: number };
} 