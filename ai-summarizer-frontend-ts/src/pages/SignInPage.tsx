import { useEffect, useState, type FormEvent } from "react";
import { useAuthStore } from "../store/authStore";
import { useNavigate } from "react-router-dom";

function SignInPage() {
  const [username, setUsernameOrEmail] = useState("");
  const [password, setPassword] = useState("");

  const {
    login,
    loading,
    error,
    clearError,
    lastAttemptedRoute,
    sessionExpired,
    clearSessionExpired,
  } = useAuthStore();

  const navigate = useNavigate();

  useEffect(() => {
    if (sessionExpired) {
      // keep existing behavior: simple alert
      alert("Session expired, please login again.");
      clearSessionExpired();
    }
  }, [sessionExpired, clearSessionExpired]);

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    try {
      await login({ username, password });
      clearError();

      const targetRoute = lastAttemptedRoute || "/";
      navigate(targetRoute, { replace: true });
    } catch {
      // store handles error state
    }
  };

  return (
    <>
      <div className="min-h-screen flex items-center justify-center">
        <div className="w-full max-w-md p-4">
          <div className="title text-xl font-mono m-3 text-center">Please Sign In</div>
          {error && (
            <div className="w-full m-3 text-center text-sm text-red-700 bg-red-100 border border-red-300 rounded p-2">
              {error}
              <button onClick={() => clearError()} className="ml-3 underline">
                Clear
              </button>
            </div>
          )}
          <form onSubmit={handleSubmit} className="w-full">
            <input
              value={username}
              onChange={(e) => setUsernameOrEmail(e.target.value)}
              placeholder="Login"
              type="text"
              className="text-sm italic w-full h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
            />
            <input
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Password"
              type="password"
              className="text-sm italic w-full h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
            />
            <div className="w-full m-3 text-right">
            <button
              type="button"
              onClick={() => navigate("/signup")}
              className="mx-3 hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 pl-5 pr-5 rounded"
            >
              Sign up
            </button>
            <button
              type="submit"
              disabled={loading}
              className="hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 pl-5 pr-5 rounded"
            >
              {loading ? "Signing in..." : "Sign in"}
            </button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
}

export default SignInPage;