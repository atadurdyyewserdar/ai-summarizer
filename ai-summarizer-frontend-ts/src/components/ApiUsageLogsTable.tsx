import React from "react";
import type { ApiUsageLog } from "../store/authStore";

interface ApiUsageLogsTableProps {
  logs: ApiUsageLog[];
}

export const ApiUsageLogsTable: React.FC<ApiUsageLogsTableProps> = ({ logs }) => (
  <div className="w-full overflow-x-auto rounded-lg">
    <table className="min-w-full text-sm border border-gray-200">
      <thead className="bg-gray-100 font-semibold text-gray-800">
        <tr>
          <th className="px-3 py-2 text-left whitespace-nowrap border-b border-gray-200">Endpoint</th>
          <th className="px-3 py-2 text-left whitespace-nowrap border-b border-gray-200">HTTP Method</th>
          <th className="px-3 py-2 text-left whitespace-nowrap border-b border-gray-200">Username</th>
          <th className="px-3 py-2 text-left whitespace-nowrap border-b border-gray-200">Processing Time (ms)</th>
          <th className="px-3 py-2 text-left whitespace-nowrap border-b border-gray-200">Time</th>
        </tr>
      </thead>
      <tbody>
        {logs.map((log, idx) => (
          <tr key={idx} className="border-b border-gray-200 text-gray-800 hover:bg-gray-50">
            <td className="px-3 py-2 text-left whitespace-nowrap">{log.endpoint}</td>
            <td className="px-3 py-2 text-left whitespace-nowrap">{log.httpMethod}</td>
            <td className="px-3 py-2 text-left whitespace-nowrap">{log.userName}</td>
            <td className="px-3 py-2 text-left whitespace-nowrap">{log.processingTime}</td>
            <td className="px-3 py-2 text-left whitespace-nowrap">{log.createdAt}</td>
          </tr>
        ))}
      </tbody>
    </table>
  </div>
);
