// User and Authentication Types
export interface User {
  id: string;
  name: string;
  email: string;
  role: "employee" | "manager";
  department?: string;
  position?: string;
}

// Profile Types
export interface EmployeeProfile {
  id: string;
  name: string;
  email: string;
  role: string;
  department?: string;
  position?: string;
  hireDate?: string;
  // Sensitive fields
  salary?: number;
  phoneNumber?: string;
  address?: string;
  emergencyContact?: string;
  bankAccount?: string;
  ssn?: string;
}

// Non-sensitive profile data for coworkers
export interface PublicProfile {
  id: string;
  name: string;
  email: string;
  department?: string;
  position?: string;
}

// Feedback Types
export interface Feedback {
  id: string;
  profileId: string;
  authorId: string;
  authorName: string;
  content: string;
  createdAt: string;
  isPolished?: boolean;
}

export interface CreateFeedbackRequest {
  profileId: string;
  content: string;
  polishWithAI?: boolean;
}

// Absence Request Types
export interface AbsenceRequest {
  id: string;
  employeeId: string;
  employeeName: string;
  startDate: string;
  endDate: string;
  reason: string;
  status: "pending" | "approved" | "rejected";
  createdAt: string;
}

export interface CreateAbsenceRequest {
  startDate: string;
  endDate: string;
  reason: string;
}

// Auth Types
export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

// Profile Update Types
export interface UpdateProfileRequest {
  name?: string;
  email?: string;
  department?: string;
  position?: string;
  phoneNumber?: string;
  address?: string;
  emergencyContact?: string;
  salary?: number;
}
