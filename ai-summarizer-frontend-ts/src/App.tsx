import { useEffect } from "react";
import { useAuthStore } from "./store/authStore";
import { useNavigate, BrowserRouter, Routes, Route } from "react-router-dom";
import MainPage from "./pages/MainPage";
import NewSummarizationPage from "./pages/NewSummarizationPage";
import SignInPage from "./pages/SignInPage";
import SignUpPage from "./pages/SignUpPage";
import ProfilePage from "./pages/ProfilePage";
import { ChangePasswordPage } from "./pages/ChangePasswordPage";
import { ProtectedRoute } from "./routes/ProtectedRoute";
import { RoleProtectedRoute } from "./routes/RoleProtectedRoute";
import DashboardPage from "./pages/DashboardPage";
import { SummarrizationPage } from "./pages/SummarrizationPage";

function AuthRedirect() {
  const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
  const navigate = useNavigate();
  useEffect(() => {
    if (isAuthenticated) {
      navigate("/summarization", { replace: true });
    } else {
      navigate("/", { replace: true });
    }
  }, [isAuthenticated, navigate]);
  return null;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<MainPage />} />
        <Route path="/summarize" element={<NewSummarizationPage />} />
        <Route path="/signin" element={<SignInPage />} />
        <Route path="/signup" element={<SignUpPage />} />
        <Route
          path="/profile"
          element={
            <ProtectedRoute>
              <ProfilePage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/change-password"
          element={
            <ProtectedRoute>
              <ChangePasswordPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/summarization"
          element={
            <ProtectedRoute>
              <SummarrizationPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/summarization/:summarizationId"
          element={
            <ProtectedRoute>
              <SummarrizationPage />
            </ProtectedRoute>
          }
        />
        <Route
          path="/dashboard"
          element={
            <RoleProtectedRoute allowedRoles={["ROLE_USER"]}>
              <DashboardPage />
            </RoleProtectedRoute>
          }
        />
        <Route path="*" element={<AuthRedirect />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
