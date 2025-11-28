import { Link, useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";
import { useRef, useState, useEffect } from "react";
import { useLocation } from "react-router-dom";

export const Navbar = () => {
    const location = useLocation();
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
    <nav className="w-full p-4 bg-black rounded-b shadow">
      <div className="flex items-center justify-between" style={{marginLeft: 'auto', marginRight: 'auto', maxWidth: '1200px'}}>
        <div className="flex items-center gap-4">
          <Link to="/" className="title text-xl font-mono text-white hover:text-yellow-300 transition-colors">AI Summarizer</Link>
        </div>

        <div className="flex items-center">
        {isAuthenticated ? (
          <div className="flex items-center relative" ref={dropdownRef}>
            <span className="text-sm mr-4 text-white">Signed in as <span className="font-semibold text-yellow-300">{user?.username}</span></span>
            <button
              className="w-10 h-10 rounded-full bg-gray-800 flex items-center justify-center focus:outline-none border-2 border-gray-400 hover:border-yellow-300 relative cursor-pointer"
              onClick={() => setDropdownOpen((open) => !open)}
            >
              <svg
                className="w-7 h-7 text-yellow-300"
                fill="currentColor"
                viewBox="0 0 24 24"
              >
                <circle cx="12" cy="8" r="4" />
                <path d="M12 14c-4 0-6 2-6 4v2h12v-2c0-2-2-4-6-4z" />
              </svg>
            </button>
            {dropdownOpen && (
              <div className="absolute right-0 top-full mt-2 w-40 bg-gray-900 border border-gray-700 rounded shadow-lg z-50">
                <button
                  onClick={handleProfile}
                  className="block w-full text-left px-4 py-2 text-sm text-white hover:bg-gray-800 cursor-pointer"
                >
                  My profile
                </button>
                {user?.role === 'ROLE_USER' && (
                  <button
                    onClick={() => {
                      navigate('/dashboard');
                      setDropdownOpen(false);
                    }}
                    className="block w-full text-left px-4 py-2 text-sm text-white hover:bg-gray-800 border-t border-gray-700 cursor-pointer"
                  >
                    Dashboard
                  </button>
                )}
                <button
                  onClick={() => {
                    navigate('/summarization');
                    setDropdownOpen(false);
                  }}
                  className="block w-full text-left px-4 py-2 text-sm text-white hover:bg-gray-800 border-t border-gray-700 cursor-pointer"
                >
                  Summaries
                </button>
                <button
                  onClick={handleLogout}
                  className="block w-full text-left px-4 py-2 text-sm text-white hover:bg-gray-800 border-t border-gray-700 cursor-pointer"
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        ) : (
          <div className="flex items-center">
            {!(location.pathname === "/signin" || location.pathname === "/signup") ? (
              <>
                <Link
                  to="/signin"
                  className="mx-3 flex items-center justify-center hover:border-yellow-300 text-center text-sm align-center cursor-pointer border-2 border-gray-400 text-white bg-black hover:bg-gray-900 hover:text-yellow-300 h-10 px-5 rounded"
                  style={{ minWidth: 90 }}
                >
                  Sign in
                </Link>
                <Link
                  to="/signup"
                  className="mx-3 flex items-center justify-center hover:border-yellow-300 text-center text-sm align-center cursor-pointer border-2 border-gray-400 text-white bg-black hover:bg-gray-900 hover:text-yellow-300 h-10 px-5 rounded"
                  style={{ minWidth: 90 }}
                >
                  Sign up
                </Link>
              </>
            ) : (
              <>
                <span className="mx-3 h-10 px-5" style={{ minWidth: 90, visibility: 'hidden' }}>Sign in</span>
                <span className="mx-3 h-10 px-5" style={{ minWidth: 90, visibility: 'hidden' }}>Sign up</span>
              </>
            )}
          </div>
        )}
      </div>
      </div>
    </nav>
  );
};