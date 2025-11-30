import React, { useState, useEffect } from "react";
import backIcon from "../assets/back.png";

import { useAuthStore } from "../store/authStore";


interface SummarizationItem {
  id: string;
  createdAt: string;
  inputText: string;
  summaryText: string;
  summaryType: string;
  documentType: string;
  fileName: string;
}

interface UserDetailsProps {
  user: any;
  onBack: () => void;
  showHeadline?: boolean;
}

export const UserDetails: React.FC<UserDetailsProps> = ({
  user,
  onBack,
  showHeadline,
}) => {
  // Local state for input fields (not saving, just for display)
  const [role, setRole] = useState(user.role || "");
  const [updateStatus, setUpdateStatus] = useState<null | 'success' | 'error'>(null);

  // Summarization history fallback
  const summarizationHistory: SummarizationItem[] =
    user.summarizationHistoryList || user.summarizationHistory || [];

  const {
    profile: profileData,
    loadingProfile: loading,
    fetchProfile,
    updateProfile,
  } = useAuthStore();

  const [formData, setFormData] = useState({
    firstName: user.firstName || user.firstname || "",
    lastName: user.lastName || user.lastname || "",
    username: user.username || "",
    email: user.email || "",
  });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  useEffect(() => {
    // Only initialize formData from profileData if form is empty (initial load)
    if (
      profileData &&
      !formData.firstName &&
      !formData.lastName &&
      !formData.email &&
      !formData.username
    ) {
      setFormData({
        email: profileData.email ?? "",
        firstName: profileData.firstname ?? "",
        lastName: profileData.lastname ?? "",
        username: profileData.username ?? "",
      });
    }
  }, [profileData]);

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setUpdateStatus(null);
    console.log(user);
    
    try {
        console.log(formData)
      await updateProfile({
        username: user?.username || "",
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
      });
      // Refetch full profile to restore summary history and all fields
      await fetchProfile();
      setUpdateStatus("success");
    } catch (err) {
      setUpdateStatus("error");
    }
  };

  return (
    <div className="w-full h-full flex items-center justify-center bg-gray-200 animate-slide-in-left">
      <div
        className="bg-white p-6 w-full h-full relative flex flex-col"
        style={{ minHeight: 400 }}
      >
        <div className="flex items-center gap-4 absolute left-4 top-4">
          <span className="text-lg font-bold text-left text-gray-800">
            User Details
          </span>
          <button
            className="flex items-center gap-2 px-3 py-1.5 bg-white border border-gray-300 hover:bg-gray-100 text-gray-900 font-semibold rounded transition-colors cursor-pointer text-sm"
            onClick={onBack}
            type="button"
          >
            Go back
            <img src={backIcon} alt="Back" className="w-3 h-3" />
          </button>
        </div>
        {showHeadline && (
          <h2 className="text-2xl font-bold mb-6 text-left mt-2 invisible">
            Users
          </h2>
        )}
        <div className="">
          <form className="flex flex-col gap-5 text-left flex-shrink-0">
            <div className="flex flex-col items-start">
              <label
                className="text-sm font-bold mb-1 text-gray-800"
                htmlFor="user-firstname"
              >
                First Name
              </label>
              <input
                id="user-firstname"
                name="firstName"
                value={formData.firstName}
                onChange={handleInputChange}
                type="text"
                className="w-full text-sm h-9 border border-gray-400 p-2 rounded bg-gray-50"
              />
            </div>
            <div className="flex flex-col items-start">
              <label
                className="text-sm font-bold mb-1 text-gray-800"
                htmlFor="user-lastname"
              >
                Last Name
              </label>
              <input
                id="user-lastname"
                name="lastName"
                value={formData.lastName}
                onChange={handleInputChange}
                type="text"
                className="w-full text-sm h-9 border border-gray-400 p-2 rounded bg-gray-50"
              />
            </div>
            <div className="flex flex-col items-start">
              <label
                className="text-sm font-bold mb-1 text-gray-800"
                htmlFor="user-email"
              >
                Email
              </label>
              <input
                id="user-email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                type="email"
                className="w-full text-sm h-9 border border-gray-400 p-2 rounded bg-gray-50"
              />
            </div>
            <div className="flex flex-col items-start">
              <label
                className="text-sm font-bold mb-1 text-gray-800"
                htmlFor="user-username"
              >
                Username
              </label>
              <input
                id="user-username"
                value={user.username || ""}
                disabled
                type="text"
                className="w-full text-sm h-9 border border-gray-400 p-2 rounded bg-gray-50"
              />
            </div>
            <div className="flex flex-col items-start">
              <label
                className="text-sm font-bold mb-1 text-gray-800"
                htmlFor="user-role"
              >
                Role
              </label>
              <input
                id="user-role"
                value={role}
                onChange={(e) => setRole(e.target.value)}
                type="text"
                disabled
                className="w-full text-xs h-8 border border-gray-400 p-2 rounded bg-gray-50"
              />
            </div>
            <div className="text-sm mt-2 text-right">
              <button onClick={handleUpdateProfile}
                className="hover:scale-101 text-sm cursor-pointer text-white rounded bg-green-700 hover:bg-green-900 py-2 px-7 "
                style={{ fontFamily: "Segoe UI, Arial, sans-serif" }}
              >
                {loading ? 'Updating...' : 'Update'}
              </button>
              {updateStatus === 'success' && (
                <span className="ml-4 text-green-700">Profile updated!</span>
              )}
              {updateStatus === 'error' && (
                <span className="ml-4 text-red-700">Update failed. Please try again.</span>
              )}
            </div>
          </form>
        </div>
        <div className="mt-12">
          <h3 className="text-lg font-semibold mb-2 text-gray-800">
            Summarization History
          </h3>
          {summarizationHistory.length === 0 ? (
            <div className="text-gray-400">No summarization history found.</div>
          ) : (
            <div className="max-h-48 overflow-y-auto border border-gray-300 rounded-sm p-2 bg-gray-50">
              <table className="min-w-full text-sm border border-gray-200 rounded-sm">
                <thead className="bg-gray-100 font-semibold text-gray-800">
                  <tr>
                    <th className="px-2 py-1 text-left">File Name</th>
                    <th className="px-2 py-1 text-left">Type</th>
                    <th className="px-2 py-1 text-left">Created At</th>
                  </tr>
                </thead>
                <tbody>
                  {summarizationHistory.map((item) => (
                    <tr key={item.id}>
                      <td className="px-2 py-1 whitespace-nowrap">
                        {item.fileName}
                      </td>
                      <td className="px-2 py-1 whitespace-nowrap">
                        {item.documentType}
                      </td>
                      <td className="px-2 py-1 whitespace-nowrap">
                        {item.createdAt}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};