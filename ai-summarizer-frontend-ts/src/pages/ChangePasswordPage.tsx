import { type FormEvent, useState } from "react";
import { useAuthStore } from "../store/authStore";

export const ChangePasswordPage = () => {
  const [oldPassword, setOldPassword] = useState("");
  const [password, setPassword] = useState("");
  const [status, setStatus] = useState<null | "success" | "error">(null);
  const { changePassword, loading, error, clearError, user } = useAuthStore();

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setStatus(null);
    try {
      // Use user?.username from persist store for safety
      await changePassword({ userName: user?.username || "", password });
      setStatus("success");
      setOldPassword("");
      setPassword("");
    } catch {
      setStatus("error");
    }
  };

  return (
    <>
      <div className="min-h-screen flex items-center justify-center">
        <div className="w-full max-w-md p-4">
          <div className="title text-xl font-mono m-3 text-center">Change Password</div>

          {error && (
            <div className="w-full m-3 text-center text-sm text-red-700 bg-red-100 border border-red-300 rounded p-2">
              {error}
              <button onClick={() => clearError()} className="ml-3 underline">
                Clear
              </button>
            </div>
          )}

          {status === "success" && (
            <div className="w-full m-3 text-center text-sm text-green-700 bg-green-100 border border-green-300 rounded p-2">
              Password updated!
            </div>
          )}
          {status === "error" && (
            <div className="w-full m-3 text-center text-sm text-red-700 bg-red-100 border border-red-300 rounded p-2">
              Change failed. Please try again.
            </div>
          )}

          <form onSubmit={handleSubmit} className="w-full">

            {/* Old password input (not used in backend call, but UI for completeness) */}
            <input
              placeholder="Current Password"
              type="password"
              className="text-sm italic w-full h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
              value={oldPassword}
              onChange={(e) => setOldPassword(e.target.value)}
            />

            <input
              placeholder="New Password"
              type="password"
              className="text-sm italic w-full h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
            />

            <div className="w-full m-3 text-right">
              <button
                type="submit"
                disabled={loading}
                className="hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 pl-5 pr-5 rounded"
              >
                {loading ? "Updating..." : "Change Password"}
              </button>
            </div>
          </form>
        </div>
      </div>
    </>
  );
};