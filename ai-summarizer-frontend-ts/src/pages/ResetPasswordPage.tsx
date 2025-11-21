import { type FormEvent, useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

export const ResetPasswordPage = () => {
  const [searchParams] = useSearchParams();
  // prefer token from persisted store (if any) then fall back to URL param
  const PERSIST_KEY = "auth-storage";

  const readTokenFromPersist = () => {
    try {
      if (typeof window === "undefined") return null;
      const raw = window.localStorage.getItem(PERSIST_KEY);
      if (!raw) return null;
      const parsed = JSON.parse(raw);

      // Zustand persist may store state under `state` key or directly
      const candidates = [
        parsed?.state?.accessToken,
        parsed?.accessToken,
      ];

      for (const c of candidates) {
        if (c) return String(c);
      }
    } catch (e) {
      // ignore parse errors
    }
    return null;
  };

  const persistedToken = readTokenFromPersist();
  const token = persistedToken || searchParams.get("token") || "";
  const navigate = useNavigate();

  const [newPassword, setNewPassword] = useState("");
  const { resetPassword, loading, error, clearError } = useAuthStore();
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    try {
      await resetPassword({ token, newPassword });
      clearError();
      setSuccess(true);

      setTimeout(() => navigate("/signin", { replace: true }), 1500);
    } catch {}
  };

  if (!token)
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="w-full max-w-md p-4">
          <div className="w-full m-3 text-center text-sm text-red-700 bg-red-100 border border-red-300 rounded p-2">Invalid token.</div>
        </div>
      </div>
    );

  return (
    <>
      <div className="min-h-screen flex items-center justify-center">
        <div className="w-full max-w-md p-4">
          <div className="title text-xl font-mono m-3 text-center">Reset Password</div>

          {error && (
            <div className="w-full m-3 text-center text-sm text-red-700 bg-red-100 border border-red-300 rounded p-2">
              {error}
              <button onClick={() => clearError()} className="ml-3 underline">
                Clear
              </button>
            </div>
          )}

          {success && (
            <div className="w-full m-3 text-center text-sm text-green-700 bg-green-100 border border-green-300 rounded p-2">
              Password updated! Redirecting…
            </div>
          )}

          <form onSubmit={handleSubmit} className="w-full">
            <input
              placeholder="New Password"
              type="password"
              className="text-sm italic w-full h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
            />

            <div className="w-full m-3 text-right">
              <button
                type="submit"
                disabled={loading}
                className="hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 pl-5 pr-5 rounded"
              >
                {loading ? "Updating..." : "Reset Password"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
};