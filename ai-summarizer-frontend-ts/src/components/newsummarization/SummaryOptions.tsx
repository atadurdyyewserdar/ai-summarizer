import React from "react";

interface SummaryOptionsProps {
  summaryType: string;
  onSummaryTypeChange: (type: string) => void;
  customSummary: string;
  onCustomSummaryChange: (val: string) => void;
  dropdownOpen: boolean;
  setDropdownOpen: (open: boolean) => void;
  dropdownRef: React.RefObject<HTMLDivElement | null>;
  summaryOptions: string[];
}

const SummaryOptions: React.FC<SummaryOptionsProps> = ({
  summaryType,
  onSummaryTypeChange,
  customSummary,
  onCustomSummaryChange,
  dropdownOpen,
  setDropdownOpen,
  dropdownRef,
  summaryOptions,
}) => (
  <div className="flex flex-col flex-1 relative" ref={dropdownRef}>
    <label htmlFor="summaryType" className="block mb-1 font-semibold text-lg">
      Summary type
    </label>
    <button
      type="button"
      className="border border-gray-600 rounded px-3 py-2 text-base bg-gray-100 focus:outline-none focus:border-gray-800 flex items-center justify-between cursor-pointer"
      onClick={() => setDropdownOpen(!dropdownOpen)}
    >
      <span>{summaryType}</span>
      <svg className="w-5 h-5 ml-2 text-gray-700" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
      </svg>
    </button>
    {dropdownOpen && (
      <div className="absolute left-0 right-0 mt-20 bg-gray-50 border border-gray-400 rounded z-50 py-1 transition-all duration-150">
        {summaryOptions.map(option => (
          <button
            key={option}
            className={`block w-full text-left px-4 py-2 text-base rounded transition-colors duration-100 cursor-pointer bg-gray-50 ${option === summaryType ? 'bg-gray-200 font-semibold text-gray-900' : 'text-gray-700'} hover:bg-gray-100`}
            onClick={() => { onSummaryTypeChange(option); setDropdownOpen(false); }}
          >
            {option}
          </button>
        ))}
      </div>
    )}
    {summaryType === "Custom" && (
      <div className="flex flex-col flex-1 mt-4">
        <label className="mb-2 text-lg font-bold">Custom Summary Instructions</label>
        <textarea
          className="min-h-32 p-4 w-full border border-blue-400 focus:border-blue-600 rounded"
          placeholder="Describe your custom summary requirements..."
          value={customSummary}
          onChange={e => onCustomSummaryChange(e.target.value)}
        />
      </div>
    )}
  </div>
);

export default SummaryOptions;