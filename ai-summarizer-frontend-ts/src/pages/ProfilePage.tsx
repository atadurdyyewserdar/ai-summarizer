import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";
import profile from "../assets/profile-picture.png";
import editGif from "../assets/edit.gif";
import fileIcon from "../assets/file.png";
import dustBinIcon from "../assets/dust-bin.png";
import { Navbar } from "../components/Navbar";

const ProfilePage = () => {
  const navigate = useNavigate();
  // Helper to get icon by documentType
  const getFileIcon = () => fileIcon;
  const {
    profile: profileData,
    loadingProfile: loading,
    profileError: error,
    fetchProfile,
    updateProfile,
    user,
    // ...existing code...
    changePassword,
  } = useAuthStore();
    const [newPassword, setNewPassword] = useState("");
    const [passwordStatus, setPasswordStatus] = useState<null | "success" | "error">(null);

    const handlePasswordChange = async (e: React.FormEvent) => {
      e.preventDefault();
      setPasswordStatus(null);
      try {
        // Use username from persist store, not formData
        await changePassword({ userName: user?.username || "", password: newPassword });
        setPasswordStatus("success");
        setNewPassword("");
      } catch {
        setPasswordStatus("error");
      }
    };
  const [isHydrated, setIsHydrated] = useState(false);

  const [isEditing] = useState({
    firstName: true,
    lastName: true,
    email: true,
  });
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    username: "",
    email: "",
  });
  const [updateStatus, setUpdateStatus] = useState<null | 'success' | 'error'>(null);

  useEffect(() => {
    // This effect ensures we wait for the store to be rehydrated from localStorage
    // before we attempt to fetch data that depends on the persisted user state.
    const unsub = useAuthStore.persist.onFinishHydration(() => {
      setIsHydrated(true);
    });

    // If the store is already hydrated, we can set the state immediately.
    if (useAuthStore.persist.hasHydrated()) {
      setIsHydrated(true);
    }

    return () => {
      unsub();
    };
  }, []);

  useEffect(() => {
    // Only fetch the profile if the store has been hydrated and a user exists.
    if (isHydrated && user?.username) {
      fetchProfile();
    }
  }, [isHydrated, user?.username, fetchProfile]);

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

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleUpdateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    setUpdateStatus(null);
    try {
      await updateProfile({
        username: formData.username,
        firstName: formData.firstName,
        lastName: formData.lastName,
        email: formData.email,
      });
      // Refetch full profile to restore summary history and all fields
      await fetchProfile();
      setUpdateStatus('success');
    } catch (err) {
      setUpdateStatus('error');
    }
  };

  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      <div className="w-full max-w-[1200px] mx-auto flex min-h-screen gap-x-8">
        <div className="min-w-1/3 h-700px relative flex flex-col items-center mt-[27px]">
          <div className="text-2xl mb flex items-center w-full text-left">
            <span>Profile picture</span>
          </div>
          <hr className="mb-5 border-gray-300 w-full" />
          <div className="relative w-[300px] h-[300px] mx-auto mb-6">
            <img
              src={profile}
              alt="Profile"
              className="w-full h-full object-cover border-1 border-gray-900 rounded-full"
            />
            <label htmlFor="profile-image-input" className="absolute bottom-2 right-2 flex items-center bg-white rounded-md shadow cursor-pointer py-1 px-5" style={{ zIndex: 2 }}>
              <img src={editGif} alt="Edit" className="w-3 h-3 mr-4" />
              <span className="text-black text-sm">Edit</span>
              <input
                id="profile-image-input"
                type="file"
                accept="image/*"
                className="hidden"
              />
            </label>
          </div>
          <div className="mx-auto text-center mb-6">
            <div className="text-xl text-black">{profileData?.username}</div>
            <div className="text-base text-gray-600">{profileData?.email}</div>
          </div>
        </div>
        <div className="min-w-2/3 flex flex-col mt-2">
          <div className="rounded-none p-5 bg-white">
            <div className="text-2xl mb flex items-center">
              <span>Personal Details</span>
            </div>
            <hr className="mb-5 border-gray-300" />
            <div className="space-y-4">
              {/* First Name */}
              <div className="flex flex-col items-start">
                <label className="text-sm font-bold mb-1">Name:</label>
                <div className="flex items-center w-full">
                  <input
                    disabled={!isEditing.firstName}
                    name="firstName"
                    value={formData.firstName}
                    onChange={handleInputChange}
                    type="text"
                    className="w-96 text-sm h-9 border-1 border-gray-400 p-2 disabled:bg-gray-100 rounded"
                    style={{ backgroundColor: "#f8f9fa" }}
                  />
                </div>
              </div>
              {/* Last Name */}
              <div className="flex flex-col items-start">
                <label className="text-sm font-bold mb-1">Last name:</label>
                <div className="flex items-center w-full">
                  <input
                    disabled={!isEditing.lastName}
                    name="lastName"
                    value={formData.lastName}
                    onChange={handleInputChange}
                    type="text"
                    className="w-96 text-sm h-9 border-1 border-gray-400 p-2 disabled:bg-gray-100 rounded"
                    style={{ backgroundColor: "#f8f9fa" }}
                  />
                </div>
              </div>
              {/* Email */}
              <div className="flex flex-col items-start">
                <label className="text-sm font-bold mb-1">Email:</label>
                <div className="flex items-center w-full">
                  <input
                    disabled={!isEditing.email}
                    name="email"
                    value={formData.email}
                    onChange={handleInputChange}
                    type="text"
                    className="w-96 text-sm h-9 border-1 border-gray-400 p-2 disabled:bg-gray-100 rounded"
                    style={{ backgroundColor: "#f8f9fa" }}
                  />
                </div>
              </div>
              {/* Set New Password */}
            </div>

            <form className="text-sm mt-5 text-left" onSubmit={handleUpdateProfile}>
              <button
                type="submit"
                className="hover:scale-101 w-45 text-sm cursor-pointer text-white rounded bg-green-700 hover:bg-green-900 py-1.5 px-7 "
                style={{ fontFamily: "Segoe UI, Arial, sans-serif" }}
                disabled={loading}
              >
                {loading ? 'Updating...' : 'Update'}
              </button>
              {updateStatus === 'success' && (
                <span className="ml-4 text-green-700">Profile updated!</span>
              )}
              {updateStatus === 'error' && (
                <span className="ml-4 text-red-700">Update failed. Please try again.</span>
              )}
            </form>
            {/* Set New Password - now under Update button */}
            <form className="flex flex-col items-start mt-5" onSubmit={handlePasswordChange}>
              <label className="text-sm font-bold mb-1 text-red-700">
                Set new password:
              </label>
              <input
                placeholder="New Password"
                type="password"
                value={newPassword}
                onChange={e => setNewPassword(e.target.value)}
                className="w-96 text-sm h-9 border-1 border-gray-400 p-2 rounded mb-2 cursor-pointer"
                style={{ backgroundColor: "#f8f9fa" }}
              />
              <button
                type="submit"
                className="mt-3 hover:scale-101 w-45 bg-green-700 hover:bg-green-900 text-sm cursor-pointer text-white py-1.5 px-7 rounded"
                style={{ fontFamily: "Segoe UI, Arial, sans-serif" }}
                disabled={loading}
              >
                Change Password
              </button>
              {passwordStatus === "success" && (
                <span className="ml-4 text-green-700">Password changed!</span>
              )}
              {passwordStatus === "error" && (
                <span className="ml-4 text-red-700">Change failed. Please try again.</span>
              )}
            </form>
          </div>
          <div className="rounded-none p-5 bg-white text-gray-500">
            <div className="text-xl text-black mb-5">Your Summaries </div>
            {loading && <div className="text-sm">Loading summaries...</div>}
            {error && <div className="text-sm text-red-700">{error}</div>}
            {!loading && !error && (
              <>
                {(profileData?.summarizationHistoryList ?? []).map((it) => (
                  <div
                    key={it.id}
                    className="hover:text-black hover:scale-102 cursor-pointer mb-1 border-gray-400 border-1 min-h-10 text-sm p-2 flex justify-between items-center"
                    style={{ fontFamily: "Segoe UI, Arial, sans-serif" }}
                  >
                    <div
                      className="flex-1"
                      onClick={() =>
                        navigate(`/summarization/${it.id}`, {
                          state: {
                            summarizationList:
                              profileData?.summarizationHistoryList ?? [],
                            selectedId: it.id,
                          },
                        })
                      }
                    >
                      <div className="flex flex-col items-start mb-2 pb-2 border-b border-gray-200">
                        <div className="flex items-center gap-2">
                          <img
                            src={getFileIcon()}
                            alt={it.documentType + " icon"}
                            className="w-4 h-4 object-contain"
                          />
                          <span className="text-base text-gray-700">
                            {it.fileName}
                          </span>
                        </div>
                      </div>
                      <div className="flex items-center mt-2">
                        <span style={{ fontSize: "0.95rem" }}>
                          {(it.summaryText || it.inputText)?.length > 80
                            ? (it.summaryText || it.inputText).slice(0, 80) +
                              "..."
                            : it.summaryText || it.inputText}
                        </span>
                        <span className="text-xs ml-2">&#8594;</span>
                      </div>
                    </div>
                    <button
                      className="ml-3 text-red-500 hover:text-red-700"
                      title="Delete summary"
                      onClick={async (e) => {
                        e.stopPropagation();
                        try {
                          await useAuthStore.getState().deleteSummarization(it.id);
                        } catch (err) {
                          alert("Failed to delete summary");
                        }
                      }}
                    >
                      <img
                        src={dustBinIcon}
                        alt="Delete"
                        className="w-4 h-4 object-contain cursor-pointer"
                        style={{ filter: 'grayscale(1)' }}
                      />
                    </button>
                  </div>
                ))}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
