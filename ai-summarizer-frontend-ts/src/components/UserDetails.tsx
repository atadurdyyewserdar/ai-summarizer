import React, { useState } from "react";
import backIcon from "../assets/back.png";

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

export const UserDetails: React.FC<UserDetailsProps> = ({ user, onBack, showHeadline }) => {
  // Local state for input fields (not saving, just for display)
  const [username, setUsername] = useState(user.username || "");
  const [firstName, setFirstName] = useState(user.firstName || user.firstname || "");
  const [lastName, setLastName] = useState(user.lastName || user.lastname || "");
  const [email, setEmail] = useState(user.email || "");
  const [role, setRole] = useState(user.role || "");

  // Summarization history fallback
  const summarizationHistory: SummarizationItem[] = user.summarizationHistoryList || user.summarizationHistory || [];

  return (
    <div className="w-full h-full flex items-center justify-center bg-gray-200 animate-slide-in-left" >
      <div className="bg-white p-6 w-full h-full relative flex flex-col" style={{ minHeight: 400 }}>
        <div className="flex items-center gap-4 absolute left-4 top-4">
          <span className="text-lg font-bold text-left">User Details</span>
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
          <h2 className="text-2xl font-bold mb-6 text-left mt-2 invisible">Users</h2>
        )}
        <div className="">
          <form className="flex flex-col gap-5 text-left flex-shrink-0">
            
            <div className="flex flex-col items-start">
              <label className="text-sm font-bold mb-1" htmlFor="user-firstname">First Name</label>
              <input
                id="user-firstname"
                value={firstName}
                onChange={e => setFirstName(e.target.value)}
                type="text"
                className="w-full text-sm h-9 border border-gray-400 p-2 rounded bg-gray-50"
                disabled
              />
            </div>
            <div className="flex flex-col items-start">
              <label className="text-sm font-bold mb-1" htmlFor="user-lastname">Last Name</label>
              <input
                id="user-lastname"
                value={lastName}
                onChange={e => setLastName(e.target.value)}
                type="text"
                className="w-full text-sm h-9 border border-gray-400 p-2 rounded bg-gray-50"
                disabled
              />
            </div>
            <div className="flex flex-col items-start">
              <label className="text-sm font-bold mb-1" htmlFor="user-email">Email</label>
              <input
                id="user-email"
                value={email}
                onChange={e => setEmail(e.target.value)}
                type="email"
                className="w-full text-sm h-9 border border-gray-400 p-2 rounded bg-gray-50"
                disabled
              />
            </div>
            <div className="flex flex-col items-start">
              <label className="text-sm font-bold mb-1" htmlFor="user-username">Username</label>
              <input
                id="user-username"
                value={username}
                onChange={e => setUsername(e.target.value)}
                type="text"
                className="w-full text-sm h-9 border border-gray-400 p-2 rounded bg-gray-50"
                disabled
              />
            </div>
            <div className="flex flex-col items-start">
              <label className="text-sm font-bold mb-1" htmlFor="user-role">Role</label>
              <input
                id="user-role"
                value={role}
                onChange={e => setRole(e.target.value)}
                type="text"
                className="w-full text-sm h-8 border border-gray-400 p-2 rounded bg-gray-50"
                disabled
              />
            </div>
          </form>
        </div>
        <div className="mt-12">
          <h3 className="text-lg font-semibold mb-2">Summarization History</h3>
          {summarizationHistory.length === 0 ? (
            <div className="text-gray-400">No summarization history found.</div>
          ) : (
            <div className="max-h-48 overflow-y-auto border rounded p-2 bg-gray-50">
              <table className="min-w-full text-sm">
                <thead>
                  <tr>
                    <th className="px-2 py-1 text-left">File Name</th>
                    <th className="px-2 py-1 text-left">Type</th>
                    <th className="px-2 py-1 text-left">Created At</th>
                  </tr>
                </thead>
                <tbody>
                  {summarizationHistory.map((item) => (
                    <tr key={item.id}>
                      <td className="px-2 py-1 whitespace-nowrap">{item.fileName}</td>
                      <td className="px-2 py-1 whitespace-nowrap">{item.documentType}</td>
                      <td className="px-2 py-1 whitespace-nowrap">{item.createdAt}</td>
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

// Tailwind animation
// .animate-slide-in-left {
//   animation: slide-in-left 0.4s cubic-bezier(0.4, 0, 0.2, 1) both;
// }
// @keyframes slide-in-left {
//   0% { transform: translateX(-100%); opacity: 0; }
//   100% { transform: translateX(0); opacity: 1; }
// }
