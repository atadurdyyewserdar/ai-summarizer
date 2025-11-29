import React from "react";

interface ResultPanelProps {
  summaryResult: string;
  typingText: string;
  isTypingDone: boolean;
  onCreateNew: () => void;
}

const ResultPanel: React.FC<ResultPanelProps> = ({
  summaryResult,
  typingText,
  isTypingDone,
  onCreateNew,
}) => (
  <>
    <div className="text-xl font-bold text-left mb-2">Summary</div>
    <div className="justify-center min-h-80 bg-white border border-gray-400 rounded mb-4">
      <div
        className="p-5 text-lg mb-8 text-gray-700 w-full text-left"
        style={{ minHeight: 80, fontFamily: "Segoe UI, Arial, sans-serif" }}
      >
        {!isTypingDone ? (
          <pre
            style={{
              whiteSpace: "pre-wrap",
              wordBreak: "break-word",
              margin: 0,
              fontFamily: "Segoe UI, Arial, sans-serif",
              fontSize: "1rem",
            }}
          >
            {typingText}
            <span className="animate-pulse">|</span>
          </pre>
        ) : (
          <pre
            style={{
              whiteSpace: "pre-wrap",
              wordBreak: "break-word",
              margin: 0,
              fontFamily: "Segoe UI, Arial, sans-serif",
              fontSize: "1rem",
            }}
          >
            {summaryResult}
          </pre>
        )}
      </div>
    </div>
    <div className="flex justify-end">
      <button
        className="px-5 py-1.5 rounded bg-green-700 text-white text-sm shadow cursor-pointer transition-colors duration-200 hover:bg-green-900"
        style={{ fontFamily: "Segoe UI, Arial, sans-serif" }}
        onClick={onCreateNew}
      >
        Create new
      </button>
    </div>
  </>
);

export default ResultPanel;
