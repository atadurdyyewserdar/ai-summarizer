// src/store/authStore.ts
import { create } from "zustand";
import { persist } from "zustand/middleware";
import { apiClient } from "../api/client";

const AUTH_PERSIST_KEY = "auth-storage";

export interface User {
  id: string;
  username: string;
  email: string;
  // add more as needed
}

interface LoginPayload {
  username: string;
  password: string;
}

interface RegisterPayload {
  username: string;
  email: string;
  password: string;
}

interface ForgotPasswordPayload {
  username: string;
}

interface ResetPasswordPayload {
  token: string;
  newPassword: string;
}

interface ChangePasswordPayload {
  oldPassword: string;
  newPassword: string;
}

interface AuthState {
  user: User | null;
  accessToken: string | null;
  refreshToken: string | null;
  isAuthenticated: boolean;
  lastAttemptedRoute: string | null;
  sessionExpired: boolean;

  // UI flags (optional)
  loading: boolean;
  error: string | null;

  // actions
  login: (data: LoginPayload) => Promise<void>;
  register: (data: RegisterPayload) => Promise<void>;
  forgotPassword: (data: ForgotPasswordPayload) => Promise<void>;
  resetPassword: (data: ResetPasswordPayload) => Promise<void>;
  changePassword: (data: ChangePasswordPayload) => Promise<void>;

  logout: (opts?: { sessionExpired?: boolean }) => void;
  setTokens: (accessToken: string | null, refreshToken: string | null) => void;
  setUser: (user: User | null) => void;
  setLastAttemptedRoute: (route: string | null) => void;
  clearError: () => void;
  clearSessionExpired: () => void;
}

const initialState: Omit<
  AuthState,
  | "login"
  | "register"
  | "forgotPassword"
  | "resetPassword"
  | "changePassword"
  | "logout"
  | "setTokens"
  | "setUser"
  | "setLastAttemptedRoute"
  | "clearError"
  | "clearSessionExpired"
> = {
  user: null,
  accessToken: null,
  refreshToken: null,
  isAuthenticated: false,
  lastAttemptedRoute: null,
  sessionExpired: false,
  loading: false,
  error: null,
};

type AuthResponse = { user: User; accessToken: string; refreshToken: string };

const getErrorMessage = (err: unknown, fallback: string) => {
  const e = err as any;
  return e?.response?.data?.message ?? e?.message ?? fallback;
};

const isAuthenticatedFromState = (state: {
  accessToken: string | null;
  refreshToken: string | null;
  user: User | null;
}) => !!(state.accessToken || state.refreshToken || state.user);

export const useAuthStore = create<AuthState>()(
  persist(
    (set, get) => ({
      ...initialState,

      async login(data) {
        try {
          set({ loading: true, error: null });

          const res = await apiClient.post<AuthResponse>("/auth/login", data);
          const resData = res.data;

          set({
            user: resData.user,
            accessToken: resData.accessToken,
            refreshToken: resData.refreshToken,
            isAuthenticated: isAuthenticatedFromState({
              accessToken: resData.accessToken,
              refreshToken: resData.refreshToken,
              user: resData.user,
            }),
            loading: false,
            error: null,
            sessionExpired: false,
          });
        } catch (err: any) {
          const message = getErrorMessage(err, "Login failed. Please try again.");
          set({ loading: false, error: message, isAuthenticated: false });
          throw err;
        }
      },

      async register(data) {
        try {
          set({ loading: true, error: null });

          const res = await apiClient.post<AuthResponse>("/auth/register", data);
          const resData = res.data;

          set({
            user: resData.user,
            accessToken: resData.accessToken,
            refreshToken: resData.refreshToken,
            isAuthenticated: isAuthenticatedFromState({
              accessToken: resData.accessToken,
              refreshToken: resData.refreshToken,
              user: resData.user,
            }),
            loading: false,
            error: null,
            sessionExpired: false,
          });
        } catch (err: any) {
          const message = getErrorMessage(err, "Registration failed. Please try again.");
          set({ loading: false, error: message });
          throw err;
        }
      },

      async forgotPassword(data) {
        try {
          set({ loading: true, error: null });

          await apiClient.post("/auth/forgot-password", data);

          set({ loading: false });
        } catch (err: any) {
          const message =
            err?.response?.data?.message ||
            "Could not send reset instructions. Please try again.";
          set({ loading: false, error: message });
          throw err;
        }
      },

      async resetPassword(data) {
        try {
          set({ loading: true, error: null });

          await apiClient.post("/auth/reset-password", data);

          set({ loading: false });
        } catch (err: any) {
          const message =
            err?.response?.data?.message ||
            "Could not reset password. Please try again.";
          set({ loading: false, error: message });
          throw err;
        }
      },

      async changePassword(data) {
        try {
          set({ loading: true, error: null });

          await apiClient.post("/auth/change-password", data);

          set({ loading: false });
        } catch (err: any) {
          const message =
            err?.response?.data?.message ||
            "Could not change password. Please try again.";
          set({ loading: false, error: message });
          throw err;
        }
      },

      logout(opts) {
        const { sessionExpired } = opts || {};
        set({
          user: null,
          accessToken: null,
          refreshToken: null,
          isAuthenticated: false,
          lastAttemptedRoute: null,
          sessionExpired: !!sessionExpired,
          loading: false,
          error: null,
        });

        if (typeof window !== "undefined") {
          try { window.localStorage.removeItem(AUTH_PERSIST_KEY); } catch {}
        }
      },

      setTokens(accessToken, refreshToken) {
        const isAuthenticated = isAuthenticatedFromState({ accessToken, refreshToken, user: get().user });
        set({ accessToken, refreshToken, isAuthenticated });
      },

      setUser(user) {
        const isAuthenticated = isAuthenticatedFromState({ accessToken: get().accessToken, refreshToken: get().refreshToken, user });
        set({ user, isAuthenticated });
      },

      setLastAttemptedRoute(route) {
        set({ lastAttemptedRoute: route });
      },

      clearError() {
        set({ error: null });
      },

      clearSessionExpired() {
        set({ sessionExpired: false });
      },
    }),
    {
      name: AUTH_PERSIST_KEY,
      partialize: (state) => ({
        user: state.user,
        accessToken: state.accessToken,
        refreshToken: state.refreshToken,
        isAuthenticated: state.isAuthenticated,
        lastAttemptedRoute: state.lastAttemptedRoute,
      }),
    }
  )
);