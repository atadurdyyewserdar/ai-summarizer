import profile from "../assets/profile.jpeg";
import { Navbar } from "../components/Navbar";
import fileIcon from "../assets/file.png";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/authStore";

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
  } = useAuthStore();
  const [isHydrated, setIsHydrated] = useState(false);

  const [isEditing, setIsEditing] = useState({
    firstName: false,
    lastName: false,
    email: false,
  });
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    username: "",
    email: "",
  });

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
    if (profileData) {
      setFormData({
        email: profileData.email ?? "",
        firstName: profileData.firstname ?? "",
        lastName: profileData.lastname ?? "",
        username: profileData.username ?? "",
      });
    }
  }, [profileData]);

  const handleEditToggle = (field: "firstName" | "lastName" | "email") => {
    setIsEditing((prev) => ({ ...prev, [field]: !prev[field] }));
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    console.log("Changed", name, "to", value);
  };

  const handleSave = async () => {
    try {
      await updateProfile(formData);
      setIsEditing({ firstName: false, lastName: false, email: false });
      fetchProfile();
    } catch (error) {
      // Error is handled in the store
    }
  };

  const imgSrc = profileData?.profileImageUrl || profile;

  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      <div className="w-full max-w-[1200px] mx-auto flex min-h-screen gap-x-8">
        <div className="min-w-1/3 h-700px mt-8">
          <img
            src={imgSrc}
            alt="Profile"
            className="mx-auto w-[300px] h-[300px] object-cover mb-6 border-1 border-gray-900"
          />
          <div className="mx-auto text-center mb-6">
            <div className="text-xl text-black">{profileData?.username}</div>
            <div className="text-base text-gray-600">{profileData?.email}</div>
          </div>
        </div>
        <div className="min-w-2/3 flex flex-col mt-8">
          <div className="rounded-none p-5 bg-white">
            <div className="text-2xl mb-2 flex items-center">
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
                  <svg
                    onClick={() => handleEditToggle("firstName")}
                    className="w-6 h-6 text-gray-800 dark:text-black cursor-pointer hover:scale-110 ml-2"
                    aria-hidden="true"
                    xmlns="http://www.w3.org/2000/svg"
                    width="16"
                    height="16"
                    fill="none"
                    viewBox="0 0 30 30"
                  >
                    <path
                      stroke="currentColor"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="m14.304 4.844 2.852 2.852M7 7H4a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h11a1 1 0 0 0 1-1v-4.5m2.409-9.91a2.017 2.017 0 0 1 0 2.853l-6.844 6.844L8 14l.713-3.565 6.844-6.844a2.015 2.015 0 0 1 2.852 0Z"
                    />
                  </svg>
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
                  <svg
                    onClick={() => handleEditToggle("lastName")}
                    className="w-6 h-6 text-gray-800 dark:text-black cursor-pointer hover:scale-110 ml-2"
                    aria-hidden="true"
                    xmlns="http://www.w3.org/2000/svg"
                    width="16"
                    height="16"
                    fill="none"
                    viewBox="0 0 30 30"
                  >
                    <path
                      stroke="currentColor"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="m14.304 4.844 2.852 2.852M7 7H4a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h11a1 1 0 0 0 1-1v-4.5m2.409-9.91a2.017 2.017 0 0 1 0 2.853l-6.844 6.844L8 14l.713-3.565 6.844-6.844a2.015 2.015 0 0 1 2.852 0Z"
                    />
                  </svg>
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
                  <svg
                    onClick={() => handleEditToggle("email")}
                    className="w-6 h-6 text-gray-800 dark:text-black cursor-pointer hover:scale-110 ml-2"
                    aria-hidden="true"
                    xmlns="http://www.w3.org/2000/svg"
                    width="16"
                    height="16"
                    fill="none"
                    viewBox="0 0 30 30"
                  >
                    <path
                      stroke="currentColor"
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      strokeWidth="2"
                      d="m14.304 4.844 2.852 2.852M7 7H4a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h11a1 1 0 0 0 1-1v-4.5m2.409-9.91a2.017 2.017 0 0 1 0 2.853l-6.844 6.844L8 14l.713-3.565 6.844-6.844a2.015 2.015 0 0 1 2.852 0Z"
                    />
                  </svg>
                </div>
              </div>
              {/* Set New Password */}
            </div>

            <div className="text-sm mt-5 text-left">
              <button
                onClick={handleSave}
                className="hover:scale-101 w-45 text-sm cursor-pointer bg-black text-white h-9 p-0 rounded"
                style={{ fontFamily: "Segoe UI, Arial, sans-serif" }}
              >
                Update
              </button>
            </div>
            {/* Set New Password - now under Update button */}
            <div className="flex flex-col items-start mt-5">
              <label className="text-sm font-bold mb-1 text-red-700">
                Set new password:
              </label>
              <input
                placeholder="New Password"
                type="text"
                className="w-96 text-sm h-9 border-1 border-gray-400 p-2 rounded mb-2 cursor-pointer"
                style={{ backgroundColor: "#f8f9fa" }}
              />
              <button
                className="mt-3 hover:scale-101 w-45 text-sm cursor-pointer bg-black text-white h-9 p-0 rounded cursor-pointer"
                style={{ fontFamily: "Segoe UI, Arial, sans-serif" }}
              >
                Submit
              </button>
            </div>
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
                    className="hover:text-black hover:scale-102 cursor-pointer mb-1 border-gray-400 border-1 min-h-10 text-sm p-2 "
                    onClick={() =>
                      navigate(`/summarization/${it.id}`, {
                        state: {
                          summarizationList:
                            profileData?.summarizationHistoryList ?? [],
                          selectedId: it.id,
                        },
                      })
                    }
                    style={{ fontFamily: "Segoe UI, Arial, sans-serif" }}
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
