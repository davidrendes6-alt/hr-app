import { authApi } from './client';
import type { LoginRequest, LoginResponse } from '../types';

export const authService = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await authApi.post<LoginResponse>('/auth/login', credentials);
    return response.data;
  },

  logout: async (): Promise<void> => {
    await authApi.post('/auth/logout');
  },

  validateToken: async (): Promise<boolean> => {
    try {
      await authApi.get('/auth/validate');
      return true;
    } catch {
      return false;
    }
  },
};
