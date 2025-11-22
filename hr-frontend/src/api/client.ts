import axios, { type AxiosInstance } from "axios";

// Base API configuration
const createApiClient = (baseURL: string): AxiosInstance => {
  const client = axios.create({ baseURL });

  // Request interceptor to add auth token
  client.interceptors.request.use(
    (config) => {
      const token = localStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => {
      return Promise.reject(error);
    }
  );

  // Response interceptor to handle errors
  client.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response?.status === 401) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.location.href = '/login';
      }
      return Promise.reject(error);
    }
  );

  return client;
};

// Service-specific API clients
export const authApi = createApiClient("http://localhost:8001"); // auth-service
export const hrApi = createApiClient("http://localhost:8002");   // hr-service
export const aiApi = createApiClient("http://localhost:8003");   // ai-service

// Legacy export for backward compatibility
export const api = hrApi;
