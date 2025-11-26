// src/api/client.ts
import axios, { AxiosError, type AxiosInstance, type AxiosRequestConfig } from "axios";
import { useAuthStore } from "../store/authStore";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

interface FailedRequestQueueItem {
  resolve: (value?: unknown) => void;
  reject: (error: unknown) => void;
  config: AxiosRequestConfig;
}

let isRefreshing = false;
let failedQueue: FailedRequestQueueItem[] = [];

const processQueue = (error: unknown, token: string | null = null) => {
  failedQueue.forEach((prom) => {
    if (error) {
      prom.reject(error);
      return;
    }

    if (token && prom.config.headers) {
      (prom.config.headers as Record<string, string>).Authorization = `Bearer ${token}`;
    }

    prom.resolve(axios(prom.config));
  });

  failedQueue = [];
};

export const apiClient: AxiosInstance = axios.create({
  baseURL: API_BASE_URL,
  withCredentials: true, // if you use cookies too
//   headers: {"Access-Control-Allow-Origin":"true"},
});

// REQUEST: attach access token
apiClient.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token && config.headers) {
    (config.headers as Record<string, string>).Authorization = `Bearer ${token}`;
  }
  return config;
});

// RESPONSE: handle 401 + silent refresh
apiClient.interceptors.response.use(
  (response) => response,
  async (error: AxiosError) => {
    const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };

    if (!error.response) {
      return Promise.reject(error);
    }

    const status = error.response.status;

    if (status !== 401 || originalRequest._retry) {
      // Not 401, or already retried once
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    const { refreshToken, setTokens, logout } = useAuthStore.getState();
    
    console.log("Refresh token:", refreshToken);

    if (!refreshToken) {
      logout({ sessionExpired: true });
      return Promise.reject(error);
    }

    if (isRefreshing) {
      // queue request while refresh is in progress
      return new Promise((resolve, reject) => {
        failedQueue.push({ resolve, reject, config: originalRequest });
      });
    }

    isRefreshing = true;
    // Debug log for refresh attempts
    if (import.meta.env.DEV) {
      // eslint-disable-next-line no-console
      console.log("[apiClient] Attempting to refresh access token...");
    }
    try {
      const refreshResponse = await axios.post(
        `${API_BASE_URL}/auth/refresh`,
        {},
        { 
          params: { refreshToken: refreshToken },
          withCredentials: true 
        }
      );

      const newAccessToken = (refreshResponse.data as any).accessToken as string;

      console.log("New access token:", newAccessToken);
      console.log("Refresh response data:", refreshResponse.data);
      const newRefreshToken =
        (refreshResponse.data as any).refreshToken || refreshToken;

      setTokens(newAccessToken, newRefreshToken);

      // Zustand persist does not expose a flush method; setTokens is sufficient for updating state and localStorage.

      processQueue(null, newAccessToken);
      isRefreshing = false;

      if (originalRequest.headers) {
        (originalRequest.headers as Record<string, string>).Authorization = `Bearer ${newAccessToken}`;
      }

      return apiClient(originalRequest);
    } catch (refreshError) {
      processQueue(refreshError, null);
      isRefreshing = false;
      logout({ sessionExpired: true });
      return Promise.reject(refreshError);
    }
  }
);