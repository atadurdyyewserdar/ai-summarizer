import { useEffect, useState, type FormEvent } from "react";
import { Navbar } from "../components/Navbar";
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
      navigate("/summarization", { replace: true });
    } catch (error) {
      // TODO store handles error state
    }
  };

  return (
    <>
      <Navbar />
      <div className="min-h-screen flex items-center justify-center">
        <div className="absolute inset-0 m-auto w-full max-w-md flex flex-col justify-center items-center p-4" style={{height: 'fit-content'}}>
          <div className="title text-xl font-mono m-3 text-center">Please Sign In</div>
          <form onSubmit={handleSubmit} className="w-full">
            {error && (
              <div className="w-96 m-3 text-left text-sm text-red-700 bg-red-100 border border-red-300 rounded p-2" style={{ textAlign: 'left' }}>
                {error}
                <button onClick={() => clearError()} className="ml-3 underline">
                  Clear
                </button>
              </div>
            )}
            <div className="flex flex-col items-start m-3">
              <label className="text-sm font-bold mb-1" htmlFor="login-username">Login</label>
              <input
                id="login-username"
                value={username}
                onChange={(e) => setUsernameOrEmail(e.target.value)}
                type="text"
                className="w-96 text-sm h-9 border-1 border-gray-400 p-2 rounded"
                style={{ backgroundColor: '#f8f9fa' }}
              />
            </div>
            <div className="flex flex-col items-start m-3">
              <label className="text-sm font-bold mb-1" htmlFor="login-password">Password</label>
              <input
                id="login-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                type="password"
                className="w-96 text-sm h-9 border-1 border-gray-400 p-2 rounded"
                style={{ backgroundColor: '#f8f9fa' }}
              />
            </div>
            <div className="flex items-center justify-end gap-3 mt-8 mb-3 w-96 ml-3">
              <button
                type="button"
                onClick={() => navigate("/signup")}
                className="hover:scale-101 w-32 text-sm cursor-pointer text-white rounded bg-green-700 hover:bg-green-900 py-1.5 px-7"
                style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}
              >
                Sign up
              </button>
              <button
                type="submit"
                disabled={loading}
                className="hover:scale-101 w-32 text-sm cursor-pointer text-white rounded bg-green-700 hover:bg-green-900 py-1.5 px-7"
                style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}
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