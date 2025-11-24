import { useState, type FormEvent } from "react";
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
      navigate("/signin", { replace: true });
    } catch {}
  };

  return (
    <>
      <div className="min-h-screen flex items-center justify-center">
        <div className="w-full max-w-md p-4">
          <div className="title text-xl font-mono m-3 text-center">
            Please, fill in forms
          </div>

          {(localError || error) && (
            <div className="w-full m-3 text-center text-sm text-red-700 bg-red-100 border border-red-300 rounded p-2">
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

          <form onSubmit={handleSubmit} className="w-full">
            <input
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              placeholder="First Name"
              type="text"
              className="text-sm italic w-full h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
            />
            <input
              value={lastName}
              onChange={(e) => setLastName(e.target.value)}
              placeholder="Last Name"
              type="text"
              className="text-sm italic w-full h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
            />
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Username"
              type="text"
              className="text-sm italic w-full h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
            />
            <input
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Email"
              type="email"
              className="text-sm italic w-full h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
            />
            <input
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="Password"
              type="password"
              className="text-sm italic w-full h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
            />
            <input
              value={repeatPassword}
              onChange={(e) => setRepeatPassword(e.target.value)}
              placeholder="Repeat Password"
              type="password"
              className="text-sm italic w-full h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
            />
            <div className="w-full m-3 text-right">
              <button
                type="submit"
                disabled={loading}
                className="mx-3 hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 pl-5 pr-5 rounded"
              >
                Sign up
              </button>
            </div>
          </form>

          <div className="font-mono text-left m-3 w-full p-1">
            <Link className="underline" to="/signin">
              I already have an account
            </Link>
          </div>
        </div>
      </div>
    </>
  );
}

export default SignUpPage;