import { User } from './user.model';

export interface DoctorAvailability {
  id: number;
  doctor: User;
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export enum DayOfWeek {
  MONDAY = 'MONDAY',
  TUESDAY = 'TUESDAY',
  WEDNESDAY = 'WEDNESDAY',
  THURSDAY = 'THURSDAY',
  FRIDAY = 'FRIDAY',
  SATURDAY = 'SATURDAY',
  SUNDAY = 'SUNDAY'
}

export interface DoctorAvailabilityRequest {
  doctorId: number;
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;
}

export interface DoctorAvailabilityResponse {
  id: number;
  doctor: User;
  dayOfWeek: DayOfWeek;
  startTime: string;
  endTime: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface AvailabilityStatistics {
  totalAvailabilities: number;
  activeAvailabilities: number;
  availabilitiesThisWeek: number;
  averageHoursPerDay: number;
  mostAvailableDay: DayOfWeek;
} 