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
  firstName: string;
  lastName: string;
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

interface UpdateProfilePayload {
  username: string;
  firstName: string;
  lastName: string;
  email: string;
}

export interface SummarizationItem {
  id: string;
  createdAt: string;
  inputText: string;
  summaryText: string;
  summaryType: string;
}

export interface ProfileData {
  id: string;
  username: string;
  email: string;
  firstname: string;
  lastname: string;
  profileImageUrl: string | null;
  createdAt: string;
  updatedAt: string;
  summarizationHistoryList: SummarizationItem[];
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

  // profile
  profile: ProfileData | null;
  loadingProfile: boolean;
  profileError: string | null;

  // actions
  login: (data: LoginPayload) => Promise<void>;
  register: (data: RegisterPayload) => Promise<void>;
  forgotPassword: (data: ForgotPasswordPayload) => Promise<void>;
  resetPassword: (data: ResetPasswordPayload) => Promise<void>;
  changePassword: (data: ChangePasswordPayload) => Promise<void>;
  fetchProfile: () => Promise<void>;
  updateProfile: (data: UpdateProfilePayload) => Promise<void>;

  logout: (opts?: { sessionExpired?: boolean }) => void;
  setTokens: (accessToken: string | null, refreshToken: string | null) => void;
  setUser: (user: User | null) => void;
  setLastAttemptedRoute: (route: string | null) => void;
  clearError: () => void;
  clearSessionExpired: () => void;

  uploadFile: (file: File, type?: string) => Promise<any>;
}

const initialState: Omit<
  AuthState,
  | "login"
  | "register"
  | "fetchProfile"
  | "updateProfile"
  | "forgotPassword"
  | "resetPassword"
  | "changePassword"
  | "logout"
  | "setTokens"
  | "setUser"
  | "setLastAttemptedRoute"
  | "clearError"
  | "clearSessionExpired"
  | "uploadFile"
> = {
  user: null,
  accessToken: null,
  refreshToken: null,
  isAuthenticated: false,
  lastAttemptedRoute: null,
  sessionExpired: false,
  loading: false,
  error: null,
  profile: null,
  loadingProfile: false,
  profileError: null,
};

type AuthResponse = {
  profile: User;
  accessToken: string;
  refreshToken: string;
};

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
          console.log("Login response data:", resData);
          // Ensure username is preserved from login payload if not in response
          const user = {
            ...resData.profile,
            username: resData.profile.username || data.username,
          };

          console.log("Derived user object:", user);

          set({
            user,
            accessToken: resData.accessToken,
            refreshToken: resData.refreshToken,
            isAuthenticated: isAuthenticatedFromState({
              accessToken: resData.accessToken,
              refreshToken: resData.refreshToken,
              user,
            }),
            loading: false,
            error: null,
            sessionExpired: false,
          });
        } catch (err: any) {
          const message = getErrorMessage(
            err,
            "Login failed. Please try again."
          );
          set({ loading: false, error: message, isAuthenticated: false });
          throw err;
        }
      },

      async register(data) {
        try {
          set({ loading: true, error: null });

          await apiClient.post("/auth/register", data);

          set({ loading: false, error: null });
        } catch (err: any) {
          const message = getErrorMessage(
            err,
            "Registration failed. Please try again."
          );
          set({ loading: false, error: message });
          throw err;
        }
      },

      async fetchProfile() {
        return new Promise<void>((resolve, reject) => {
          const performFetch = async (state?: AuthState) => {
            try {
              set({ loadingProfile: true, profileError: null });
              const finalState = state || get();
              const user = finalState.user;

              if (!user?.username) {
                throw new Error("No username available");
              }
              const res = await apiClient.get(
                `/user/profile?username=${user.username}`
              );
              const payload =
                res.data && res.data.data ? res.data.data : res.data;
              set({ profile: payload as ProfileData, loadingProfile: false });
              console.log("Fetched profile:", payload);
              resolve();
            } catch (err: any) {
              const message =
                err?.response?.data?.message ||
                err?.message ||
                "Could not load profile";
              set({ profileError: message, loadingProfile: false });
              reject(err);
            }
          };

          if (useAuthStore.persist.hasHydrated()) {
            performFetch();
          } else {
            const unsub = useAuthStore.persist.onFinishHydration((state) => {
              performFetch(state as AuthState);
              unsub(); // Unsubscribe to avoid memory leaks
            });
          }
        });
      },

      async updateProfile(data) {
        try {
          set({ loadingProfile: true, profileError: null });
          const user = get().user;
          data.username = user?.username || data.username;
          if (!user?.username) {
            throw new Error("User not authenticated");
          }
          const res = await apiClient.post(`/user/update-profile`, data);
          const payload = res.data && res.data.data ? res.data.data : res.data;
          set({ profile: payload as ProfileData, loadingProfile: false });
        } catch (err: any) {
          const message =
            err?.response?.data?.message ||
            err?.message ||
            "Could not update profile";
          set({ profileError: message, loadingProfile: false });
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
          try {
            window.localStorage.removeItem(AUTH_PERSIST_KEY);
          } catch {}
        }
      },

      // Implementation:
      async uploadFile(file, type = "BRIEF") {
        try {
          const formData = new FormData();
          formData.append("file", file);
          formData.append("userName", get().user?.username || "");
          formData.append("type", type);

          const res = await apiClient.post("/v1/documents/summarize", formData, {
            headers: { "Content-Type": "multipart/form-data" },
          });
          // Return the full backend response data
          return res.data;
        } catch (err) {
          throw err;
        }
      },

      setTokens(accessToken, refreshToken) {
        const isAuthenticated = isAuthenticatedFromState({
          accessToken,
          refreshToken,
          user: get().user,
        });
        set({ accessToken, refreshToken, isAuthenticated });
      },

      setUser(user) {
        const isAuthenticated = isAuthenticatedFromState({
          accessToken: get().accessToken,
          refreshToken: get().refreshToken,
          user,
        });
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
