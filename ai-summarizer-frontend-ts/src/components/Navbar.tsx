import { Link, useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";
import { useRef, useState, useEffect } from "react";

export const Navbar = () => {
  const { user, isAuthenticated, logout } = useAuthStore();
  const navigate = useNavigate();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const handleLogout = () => {
    logout();
    navigate("/signin", { replace: true });
  };

  const handleProfile = () => {
    navigate("/profile");
    setDropdownOpen(false);
  };

  // Close dropdown if clicked outside
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setDropdownOpen(false);
      }
    }
    if (dropdownOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [dropdownOpen]);

  return (
    <nav className="container mx-auto p-4 flex items-center justify-between">
      <div className="flex items-center gap-4">
        <div className="title text-xl font-mono">AI Summarizer</div>
      </div>

      <div className="flex items-center">
        {isAuthenticated ? (
          <div className="flex items-center relative" ref={dropdownRef}>
            <span className="text-sm mr-4">Signed in as <span className="font-semibold">{user?.username}</span></span>
            <button
              className="w-10 h-10 rounded-full bg-gray-300 flex items-center justify-center focus:outline-none border-2 border-gray-600 hover:border-gray-800 relative"
              onClick={() => setDropdownOpen((open) => !open)}
            >
              <svg
                className="w-7 h-7 text-gray-700"
                fill="currentColor"
                viewBox="0 0 24 24"
              >
                <circle cx="12" cy="8" r="4" />
                <path d="M12 14c-4 0-6 2-6 4v2h12v-2c0-2-2-4-6-4z" />
              </svg>
            </button>
            {dropdownOpen && (
              <div className="absolute right-0 top-full mt-2 w-40 bg-white border border-gray-300 rounded shadow-lg z-50">
                <button
                  onClick={handleProfile}
                  className="block w-full text-left px-4 py-2 hover:bg-gray-100"
                >
                  My profile
                </button>
                <button
                  onClick={handleLogout}
                  className="block w-full text-left px-4 py-2 hover:bg-gray-100 border-t border-gray-200"
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        ) : (
          <div className="flex items-center">
            <Link
              to="/signin"
              className="mx-3 flex items-center justify-center hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 px-5 rounded"
              style={{ minWidth: 90 }}
            >
              Sign in
            </Link>

            <Link
              to="/signup"
              className="mx-3 flex items-center justify-center hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 px-5 rounded"
              style={{ minWidth: 90 }}
            >
              Sign up
            </Link>
          </div>
        )}
      </div>
    </nav>
  );
};