import { User } from './user.model';

export interface EMR {
  id: number;
  patient: User;
  doctor: User;
  diagnosis: string;
  symptoms: string;
  treatment: string;
  prescriptions?: string;
  labOrders?: string;
  imagingOrders?: string;
  notes?: string;
  followUpDate?: string;
  followUpNotes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface EMRRequest {
  diagnosis: string;
  symptoms: string;
  treatment: string;
  prescriptions?: string;
  labOrders?: string;
  imagingOrders?: string;
  notes?: string;
  followUpDate?: string;
  followUpNotes?: string;
  doctorId: number;
  patientId: number;
}

export interface EMRResponse {
  id: number;
  patient: User;
  doctor: User;
  diagnosis: string;
  symptoms: string;
  treatment: string;
  prescriptions?: string;
  labOrders?: string;
  imagingOrders?: string;
  notes?: string;
  followUpDate?: string;
  followUpNotes?: string;
  createdAt: string;
  updatedAt: string;
}

export interface EMRStatistics {
  totalEMRs: number;
  emrsThisMonth: number;
  emrsThisYear: number;
  averageEMRsPerDay: number;
  topDiagnoses: string[];
}

export interface PatientMedicalHistory {
  patient: User;
  totalEMRs: number;
  emrs: EMR[];
}

export interface DoctorEMRSummary {
  doctor: User;
  totalEMRs: number;
  emrsThisMonth: number;
  emrsThisYear: number;
  averageEMRsPerDay: number;
} 