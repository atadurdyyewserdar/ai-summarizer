import { Link, useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

export const Navbar = () => {
  const { user, isAuthenticated, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/signin", { replace: true });
  };

  return (
    <nav className="container mx-auto p-4 flex items-center justify-between">
      <div className="flex items-center gap-4">
        <div className="title text-xl font-mono">AI Summarizer</div>
      </div>

      <div className="flex items-center">
        {isAuthenticated ? (
          <div className="flex items-center">
            <span className="text-sm mr-4">Signed in as <span className="font-semibold">{user?.username}</span></span>
            <button
              onClick={handleLogout}
              className="mx-3 hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 pl-5 pr-5 rounded"
            >
              Logout
            </button>
          </div>
        ) : (
          <div className="flex items-center">
            <Link
              to="/signin"
              className="mx-3 hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 pl-5 pr-5 rounded"
            >
              Sign in
            </Link>

            <Link
              to="/signup"
              className="mx-3 hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 pl-5 pr-5 rounded"
            >
              Sign up
            </Link>
          </div>
        )}
      </div>
    </nav>
  );
};