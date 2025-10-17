import React from "react";
import profile from "./assets/profile.jpeg";

const ProfilePage = () => {
  return (
    <div className="font-mono container min-w-700px min-h-screen flex mx-auto justify-between p-5">
      <div className="border-1 border-gray-400 min-w-1/3 h-700px m-5 p-5">
        <img
          src={profile}
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
            className="text-sm italic w-70 h-10 border-1 border-gray-400 p-2 mb-2"
          />
          <button className="hover:scale-101 w-70 mb-8 text-sm cursor-pointer bg-black text-white h-9 p-0 rounded">
            Submit
          </button>
        </div>
      </div>
      <div className="min-w-2/3 h-200px border-1 border-gray-400 m-5 p-5">
        <div className="mb-10">
          <div className="font-mono text-xl mb-5">Personal Details</div>
          <div className="font-mono text-sm mb-3 flex gap-2 items-center">
            <div>Name:</div>
            <input
              disabled
              placeholder="John"
              type="text"
              className="text-sm italic w-70 h-10 border-1 border-gray-400 p-2 mb-2 disabled:bg-gray-100"
            />
            <svg
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
              disabled
              placeholder="Doe"
              type="text"
              className="text-sm italic w-70 h-10 border-1 border-gray-400 p-2 mb-2 disabled:bg-gray-100"
            />
            <svg
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
          <div className="text-sm mb-3 text-right">
            <button className="hover:scale-101 w-45 mb-8 text-sm cursor-pointer bg-black text-white h-9 p-0">
              Save changes
            </button>
          </div>
        </div>
        <div className="font-mono text-gray-500">
          <div className="text-xl text-black mb-5">Your Summaries </div>
          <div className="hover:text-black hover:scale-102 cursor-pointer mb-1 border-gray-400 border-1 min-h-10 text-sm p-2 ">
            Artificial Intelligence <span class="text-sm">&#8594;</span>
          </div>
          <div className="hover:text-black hover:scale-102 cursor-pointer mb-1 border-gray-400 border-1 min-h-10 text-sm p-2 ">
            Lorem ipsum dolor sit, amet consectetur adipisicing elit. Aperiam
            dicta dolores cupiditate illum? Similique quasi laboriosam
            assumenda, aut culpa reiciendis eum maiores ducimus debitis beatae
            non laborum ad enim dicta? <span class="text-sm">&#8594;</span>
          </div>
          <div className="hover:text-black hover:scale-102 cursor-pointer mb-1 border-gray-400 border-1 min-h-10 text-sm p-2 ">
            Artificial Intelligence <span class="text-sm">&#8594;</span>
          </div>
          <div className="hover:text-black hover:scale-102 cursor-pointer mb-1 border-gray-400 border-1 min-h-10 text-sm p-2 ">
            Artificial Intelligence <span class="text-sm">&#8594;</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;
