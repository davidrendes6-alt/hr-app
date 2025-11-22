import { hrApi } from './client';
import type {
  EmployeeProfile,
  PublicProfile,
  UpdateProfileRequest,
  AbsenceRequest,
  CreateAbsenceRequest,
  Feedback,
} from '../types';

export const hrService = {
  // Profile endpoints
  getMyProfile: async (): Promise<EmployeeProfile> => {
    const response = await hrApi.get<EmployeeProfile>('/profiles/me');
    return response.data;
  },

  getProfileById: async (id: string): Promise<EmployeeProfile | PublicProfile> => {
    const response = await hrApi.get<EmployeeProfile | PublicProfile>(`/profiles/${id}`);
    return response.data;
  },

  updateProfile: async (id: string, data: UpdateProfileRequest): Promise<EmployeeProfile> => {
    const response = await hrApi.put<EmployeeProfile>(`/profiles/${id}`, data);
    return response.data;
  },

  // Feedback endpoints
  getFeedbackForProfile: async (profileId: string): Promise<Feedback[]> => {
    const response = await hrApi.get<Feedback[]>(`/profiles/${profileId}/feedback`);
    return response.data;
  },

  createFeedback: async (profileId: string, content: string, polishWithAI?: boolean): Promise<Feedback> => {
    const response = await hrApi.post<Feedback>(`/profiles/${profileId}/feedback`, {
      content,
      polishWithAI,
    });
    return response.data;
  },

  // Absence request endpoints
  getMyAbsenceRequests: async (): Promise<AbsenceRequest[]> => {
    const response = await hrApi.get<AbsenceRequest[]>('/absences/me');
    return response.data;
  },

  getPendingAbsenceRequests: async (): Promise<AbsenceRequest[]> => {
    const response = await hrApi.get<AbsenceRequest[]>('/absences/pending');
    return response.data;
  },

  createAbsenceRequest: async (data: CreateAbsenceRequest): Promise<AbsenceRequest> => {
    const response = await hrApi.post<AbsenceRequest>('/absences', data);
    return response.data;
  },

  approveAbsenceRequest: async (id: string): Promise<void> => {
    await hrApi.patch(`/absences/${id}/approve`);
  },

  rejectAbsenceRequest: async (id: string): Promise<void> => {
    await hrApi.patch(`/absences/${id}/reject`);
  },

  // Employee list for managers
  getAllEmployees: async (): Promise<PublicProfile[]> => {
    const response = await hrApi.get<PublicProfile[]>('/profiles');
    return response.data;
  },
};
