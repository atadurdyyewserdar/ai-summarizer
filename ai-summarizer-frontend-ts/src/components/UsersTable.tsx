import React from "react";

interface UsersTableProps {
  users: any[];
  onSelect: (user: any) => void;
}

export const UsersTable: React.FC<UsersTableProps> = ({ users, onSelect }) => (
  <div className="w-full overflow-x-auto rounded-lg">
    <table className="min-w-full text-sm border border-gray-200">
      <thead className="bg-gray-100 text-gray-800">
        <tr>
          <th className="px-3 py-2 text-left whitespace-nowrap min-w-[110px] border-b border-gray-200">Username</th>
          <th className="px-3 py-2 text-left whitespace-nowrap min-w-[110px] border-b border-gray-200">First Name</th>
          <th className="px-3 py-2 text-left whitespace-nowrap min-w-[110px] border-b border-gray-200">Last Name</th>
          <th className="px-3 py-2 text-left whitespace-nowrap min-w-[160px] border-b border-gray-200">Email</th>
        </tr>
      </thead>
      <tbody>
        {users.map((user) => (
          <tr key={user.username} className="hover:bg-gray-50 cursor-pointer border-b border-gray-200" onClick={() => onSelect(user)}>
            <td className="px-3 py-2 text-left whitespace-nowrap">{user.username}</td>
            <td className="px-3 py-2 text-left whitespace-nowrap">{user.firstName}</td>
            <td className="px-3 py-2 text-left whitespace-nowrap">{user.lastName}</td>
            <td className="px-3 py-2 text-left whitespace-nowrap">{user.email}</td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);
