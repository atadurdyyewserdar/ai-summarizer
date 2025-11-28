import { useState, useEffect } from "react";
import { useAuthStore } from "../store/authStore";
import { Navbar } from "../components/Navbar";
import { UserDetails } from "../components/UserDetails";
import { UsersTable } from "../components/UsersTable";
import { ApiUsageLogsTable } from "../components/ApiUsageLogsTable";
import type { ApiUsageLog } from "../store/authStore";

const DASHBOARD_ITEMS = [
  { id: "users", label: "Users" },
  { id: "api-usage", label: "Api usage" },
];

export default function DashboardPage() {
  const [selectedId, setSelectedId] = useState<string | null>(DASHBOARD_ITEMS[0].id);
  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedUser, setSelectedUser] = useState<any | null>(null);
  const [apiUsageLogs, setApiUsageLogs] = useState<ApiUsageLog[]>([]);
  const [apiUsageLoading, setApiUsageLoading] = useState(false);
  const [apiUsageError, setApiUsageError] = useState<string | null>(null);
  const getUsers = useAuthStore((s) => s.getUsers);
  const getApiUsageLogs = useAuthStore((s) => s.getApiUsageLogs);

  useEffect(() => {
    const fetchData = async () => {
      if (selectedId === "users") {
        setLoading(true);
        setError(null);
        try {
          const data = await getUsers();
          setUsers(data);
        } catch {
          setError("Failed to fetch users");
        } finally {
          setLoading(false);
        }
      }
      if (selectedId === "api-usage") {
        setApiUsageLoading(true);
        setApiUsageError(null);
        try {
          const data = await getApiUsageLogs();
          setApiUsageLogs(data);
        } catch {
          setApiUsageError("Failed to fetch API usage logs");
        } finally {
          setApiUsageLoading(false);
        }
      }
    };
    fetchData();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [selectedId]);

  const handleOnBack = async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await getUsers();
      setUsers(data);
    } catch {
      setError("Failed to fetch users");
    } finally {
      setLoading(false);
      setSelectedUser(null);
    }
  };

  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      <div className="flex justify-center items-start min-h-[calc(100vh-80px)]">
        <div className="w-full max-w-[1200px] mx-auto bg-white border border-gray-300 rounded-lg flex min-h-[400px]" style={{ marginTop: 32, height: 'calc(100vh - 120px)' }}>
          {/* Left: List */}
          <div className="w-1/4 border-r border-gray-200 p-0 flex flex-col min-w-[120px]" style={{ flexBasis: 'auto' }}>
            <div className="p-4 pb-2">
              <div className="text-lg font-semibold text-black">Dashboard</div>
            </div>
            <div className="flex-1 overflow-y-auto p-4 pt-0">
              {DASHBOARD_ITEMS.map((item) => (
                <div
                  key={item.id}
                  className={`flex items-center gap-2 p-2 mb-2 rounded cursor-pointer transition-all justify-between ${selectedId === item.id ? "bg-gray-200 border-l-4 border-blue-500 font-medium" : "hover:bg-gray-100"}`}
                  onClick={() => setSelectedId(item.id)}
                >
                  <span className="text-base text-gray-800" style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}>{item.label}</span>
                </div>
              ))}
            </div>
          </div>
          {/* Right: Content Panel */}
          <div className="w-3/4 min-w-0 p-3 flex flex-col h-full overflow-x-auto relative" style={{ wordBreak: 'break-word', overflowWrap: 'break-word', maxHeight: 'calc(100vh - 120px)' }}>
            <div className="w-full h-full">
              {selectedId === "users" && (
                <div>
                  {loading && <div className="text-gray-400">Loading...</div>}
                  {error && <div className="text-red-500">{error}</div>}
                  {!loading && !error && users.length > 0 && !selectedUser && (
                    <UsersTable users={users} onSelect={setSelectedUser} />
                  )}
                  {selectedUser && (
                    <UserDetails user={selectedUser} onBack={handleOnBack} showHeadline />
                  )}
                  {!loading && !error && users.length === 0 && (
                    <div className="text-gray-400">No users found.</div>
                  )}
                </div>
              )}
              {selectedId === "api-usage" && (
                <div className="flex flex-col items-center h-full w-full text-gray-700 text-2xl">
                  {/* Blank content for API usage */}
                  {apiUsageLoading && <div className="text-gray-400">Loading...</div>}
                  {apiUsageError && <div className="text-red-500">{apiUsageError}</div>}
                  {!apiUsageLoading && !apiUsageError && apiUsageLogs.length > 0 && (
                    <ApiUsageLogsTable logs={apiUsageLogs} />
                  )}
                  {!apiUsageLoading && !apiUsageError && apiUsageLogs.length === 0 && (
                    <div className="text-gray-400">No API usage logs found.</div>
                  )}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
