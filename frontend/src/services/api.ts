import axios from 'axios';
import { JournalEntry, User, UserDTO, LoginRequest } from '../types';

const API_BASE_URL = 'http://localhost:8080';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add auth token
api.interceptors.request.use(
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

// Response interceptor to handle auth errors
api.interceptors.response.use(
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

// Auth APIs
export const authAPI = {
  signup: (userData: UserDTO) => api.post('/public/signup', userData),
  login: (credentials: LoginRequest) => api.post('/public/login', credentials),
  healthCheck: () => api.get('/public/health-check'),
};

// User APIs
export const userAPI = {
  getProfile: () => api.get('/user'),
  updateProfile: (userData: User) => api.put('/user', userData),
  deleteProfile: () => api.delete('/user'),
};

// Journal APIs
export const journalAPI = {
  getAllEntries: () => api.get('/journal'),
  getEntryById: (id: string) => api.get(`/journal/id/${id}`),
  createEntry: (entry: JournalEntry) => api.post('/journal', entry),
  updateEntry: (id: string, entry: JournalEntry) => api.put(`/journal/id/${id}`, entry),
  deleteEntry: (id: string) => api.delete(`/journal/id/${id}`),
};

export default api;
