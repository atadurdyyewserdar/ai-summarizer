import { useState, type FormEvent } from "react";
import { Navbar } from "../components/Navbar";
import { Link, useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

function SignUpPage() {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");
  const [localError, setLocalError] = useState<string | null>(null);

  const { register, loading, error, clearError } = useAuthStore();
  const navigate = useNavigate();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setLocalError(null);
    try {
      if (password !== repeatPassword) {
        setLocalError("Passwords do not match");
        return;
      }

      await register({
        firstName,
        lastName,
        username,
        email,
        password,
      });
      clearError();
      navigate("/summarization", { replace: true });
    } catch {}
  };

  return (
    <>
      <Navbar />
      <div className="min-h-screen flex items-center justify-center">
        <div className="absolute inset-0 m-auto w-full max-w-md flex flex-col justify-center items-center p-4" style={{height: 'fit-content'}}>
          <div className="title text-xl font-mono m-3 text-center">
            Please, fill in forms
          </div>

          <form onSubmit={handleSubmit} className="w-full">
            {(localError || error) && (
              <div className="w-96 m-3 ml-3 text-left text-sm text-red-700 bg-red-100 border border-red-300 rounded p-2" style={{ textAlign: 'left' }}>
                {localError || error}
                <button
                  onClick={() => {
                    setLocalError(null);
                    clearError();
                  }}
                  className="ml-3 underline"
                >
                  Clear
                </button>
              </div>
            )}
            <div className="flex flex-col items-start m-3">
              <label className="text-sm font-bold mb-1" htmlFor="firstName">First Name</label>
              <input
                id="firstName"
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                type="text"
                className="w-96 text-sm h-9 border-1 border-gray-400 p-2 rounded"
                style={{ backgroundColor: '#f8f9fa' }}
              />
            </div>
            <div className="flex flex-col items-start m-3">
              <label className="text-sm font-bold mb-1" htmlFor="lastName">Last Name</label>
              <input
                id="lastName"
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                type="text"
                className="w-96 text-sm h-9 border-1 border-gray-400 p-2 rounded"
                style={{ backgroundColor: '#f8f9fa' }}
              />
            </div>
            <div className="flex flex-col items-start m-3">
              <label className="text-sm font-bold mb-1" htmlFor="username">Username</label>
              <input
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                type="text"
                className="w-96 text-sm h-9 border-1 border-gray-400 p-2 rounded"
                style={{ backgroundColor: '#f8f9fa' }}
              />
            </div>
            <div className="flex flex-col items-start m-3">
              <label className="text-sm font-bold mb-1" htmlFor="email">Email</label>
              <input
                id="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                type="email"
                className="w-96 text-sm h-9 border-1 border-gray-400 p-2 rounded"
                style={{ backgroundColor: '#f8f9fa' }}
              />
            </div>
            <div className="flex flex-col items-start m-3">
              <label className="text-sm font-bold mb-1" htmlFor="password">Password</label>
              <input
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                type="password"
                className="w-96 text-sm h-9 border-1 border-gray-400 p-2 rounded"
                style={{ backgroundColor: '#f8f9fa' }}
              />
            </div>
            <div className="flex flex-col items-start m-3">
              <label className="text-sm font-bold mb-1" htmlFor="repeatPassword">Repeat Password</label>
              <input
                id="repeatPassword"
                value={repeatPassword}
                onChange={(e) => setRepeatPassword(e.target.value)}
                type="password"
                className="w-96 text-sm h-9 border-1 border-gray-400 p-2 rounded"
                style={{ backgroundColor: '#f8f9fa' }}
              />
            </div>
            <div className="flex items-center justify-between mt-8 mb-3 w-96 ml-3">
              <Link
                className="underline text-sm"
                style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}
                to="/signin"
              >
                I already have an account
              </Link>
              <button
                type="submit"
                disabled={loading}
                className="hover:scale-101 w-32 text-sm cursor-pointer text-white rounded bg-green-700 hover:bg-green-900 py-1.5 px-7"
                style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}
              >
                {loading ? "Signing up..." : "Sign up"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
}

export default SignUpPage;