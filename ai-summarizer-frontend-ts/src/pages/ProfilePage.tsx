import profile from "../assets/profile.jpeg";
import { useEffect, useState } from "react";
import { useAuthStore } from "../store/authStore";

const ProfilePage = () => {
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
  });
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    username:"",
    email:"",
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
        username: profileData.username ?? ""
      });
    }
  }, [profileData]);

  const handleEditToggle = (field: "firstName" | "lastName") => {
    setIsEditing((prev) => ({ ...prev, [field]: !prev[field] }));
  };

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    console.log("Changed", name, "to", value);
  };

  const handleChangeEmail = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData((prev) => ({ ...prev, email: e.target.value }));
    console.log("Changed", "email", "to", e.target.value);
  }

  const handleSave = async () => {
    try {
      await updateProfile(formData);
      setIsEditing({ firstName: false, lastName: false });
      fetchProfile();
    } catch (error) {
      // Error is handled in the store
    }
  };

  const imgSrc = profileData?.profileImageUrl || profile;

  return (
    <div className="font-mono container min-w-700px min-h-screen flex mx-auto justify-between p-5">
      <div className="border-1 border-gray-400 min-w-1/3 h-700px m-5 p-5">
        <img
          src={imgSrc}
          alt="Profile"
          className="mx-auto w-[300px] h-[300px] object-cover mb-10 border-1 border-gray-900"
        />
        <div className="mx-auto text-center font-mono text-2xl m-5">
          <div className="text-sm mb-3">Set new password</div>
          <input
            placeholder="New Password"
            type="text"
            className="text-sm italic w-70 h-10 border-1 border-gray-400 p-2 mb-2"
          />
          <button className="w-70 mb-8 hover:scale-101 text-sm cursor-pointer bg-black text-white h-9 p-0">
            Submit
          </button>
          <div className="text-sm mb-3">Set your email</div>
          <input
            placeholder="Email"
            type="text"
            // defaultValue={formData?.email ?? ""}
            value={formData.email}
            className="text-sm italic w-70 h-10 border-1 border-gray-400 p-2 mb-2"
            onChange={(e) => handleChangeEmail(e)}
          />
          <button className="hover:scale-101 w-70 mb-8 text-sm cursor-pointer bg-black text-white h-9 p-0 rounded">
            Submit
          </button>
        </div>
      </div>
      <div className="min-w-2/3 h-200px border-1 border-gray-400 m-5 p-5">
        <div className="mb-10">
          <div className="font-mono text-xl mb-5 flex justify-between items-center">
            <span>Personal Details</span>
            <button
              onClick={() => fetchProfile()}
              disabled={loading}
              className="text-sm p-2 rounded-md bg-gray-200 hover:bg-gray-300 disabled:opacity-50"
            >
              Refresh
            </button>
          </div>
          <div className="font-mono text-sm mb-3 flex gap-2 items-center">
            <div>Name:</div>
            <input
              disabled={!isEditing.firstName}
              name="firstName"
              value={formData.firstName}
              onChange={handleInputChange}
              type="text"
              className="text-sm italic w-70 h-10 border-1 border-gray-400 p-2 mb-2 disabled:bg-gray-100"
            />
            <svg
              onClick={() => handleEditToggle("firstName")}
              className="w-6 h-6 text-gray-800 dark:text-black cursor-pointer hover:scale-110"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="none"
              viewBox="0 0 30 30"
            >
              <path
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="m14.304 4.844 2.852 2.852M7 7H4a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h11a1 1 0 0 0 1-1v-4.5m2.409-9.91a2.017 2.017 0 0 1 0 2.853l-6.844 6.844L8 14l.713-3.565 6.844-6.844a2.015 2.015 0 0 1 2.852 0Z"
              />
            </svg>
          </div>
          <div className="font-mono text-sm mb-3 flex gap-2 items-center">
            <div>Last name:</div>
            <input
              disabled={!isEditing.lastName}
              name="lastName"
              value={formData.lastName}
              onChange={handleInputChange}
              type="text"
              className="text-sm italic w-70 h-10 border-1 border-gray-400 p-2 mb-2 disabled:bg-gray-100"
            />
            <svg
              onClick={() => handleEditToggle("lastName")}
              className="w-6 h-6 text-gray-800 dark:text-black cursor-pointer hover:scale-110"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="none"
              viewBox="0 0 30 30"
            >
              <path
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="m14.304 4.844 2.852 2.852M7 7H4a1 1 0 0 0-1 1v10a1 1 0 0 0 1 1h11a1 1 0 0 0 1-1v-4.5m2.409-9.91a2.017 2.017 0 0 1 0 2.853l-6.844 6.844L8 14l.713-3.565 6.844-6.844a2.015 2.015 0 0 1 2.852 0Z"
              />
            </svg>
          </div>
          <div className="font-mono text-sm mb-3 flex gap-2 items-center">
            <div>username:</div>
            <input
              disabled
              value={profileData?.username ?? ""}
              type="text"
              className="text-sm italic w-70 h-10 border-1 border-gray-400 p-2 mb-2 disabled:bg-gray-100"
            />
          </div>

          <div className="text-sm mb-3 text-right">
            <button
              onClick={handleSave}
              className="hover:scale-101 w-45 mb-8 text-sm cursor-pointer bg-black text-white h-9 p-0"
            >
              Save changes
            </button>
          </div>
        </div>
        <div className="font-mono text-gray-500">
          <div className="text-xl text-black mb-5">Your Summaries </div>
          {loading && <div className="text-sm">Loading summaries...</div>}
          {error && <div className="text-sm text-red-700">{error}</div>}
          {!loading && !error && (
            <>
              {(profileData?.summarizationHistoryList ?? []).map((it) => (
                <div key={it.id} className="hover:text-black hover:scale-102 cursor-pointer mb-1 border-gray-400 border-1 min-h-10 text-sm p-2 ">
                  {it.summaryText || it.inputText} <span className="text-sm">&#8594;</span>
                </div>
              ))}
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;