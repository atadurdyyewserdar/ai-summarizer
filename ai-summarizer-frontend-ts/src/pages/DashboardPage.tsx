import { useState } from "react";
import { Navbar } from "../components/Navbar";

const DASHBOARD_ITEMS = [
  { id: "users", label: "Users" },
  { id: "api-usage", label: "Api usage" },
];

export default function DashboardPage() {
  const [selectedId, setSelectedId] = useState<string | null>(null);

  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      <div className="flex justify-center items-start min-h-[calc(100vh-80px)]">
        <div className="w-full max-w-[1200px] mx-auto bg-white border border-gray-300 rounded-lg flex min-h-[400px]" style={{ marginTop: 32, height: 'calc(100vh - 120px)' }}>
          {/* Left: List */}
          <div className="w-1/3 border-r border-gray-200 p-0 flex flex-col min-w-[180px]" style={{ flexBasis: 'auto' }}>
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
          {/* Right: Blank Page */}
          <div className="w-2/3 min-w-0 p-3 flex flex-col h-full overflow-x-auto" style={{ wordBreak: 'break-word', overflowWrap: 'break-word' }}>
            {/* Show blank for now, will update later */}
            {selectedId && (
              <div className="flex flex-col items-center justify-center h-full w-full text-gray-400 text-2xl font-semibold">
                {/* Blank content for selected item */}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
