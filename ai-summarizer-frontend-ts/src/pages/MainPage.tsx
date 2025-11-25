import { Navbar } from "../components/Navbar";
import { useAuthStore } from "../store/authStore";
import { useState, useEffect, useRef } from "react";

const SUMMARY_OPTIONS = [
  "Comprehensive",
  "Brief",
  "Key Points",
  "Executive",
  "Sentiment",
  "Technical",
  "Custom",
];

function MainPage() {
  const uploadFile = useAuthStore((state) => state.uploadFile);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [summaryType, setSummaryType] = useState<string>(SUMMARY_OPTIONS[0]);
  const [customSummary, setCustomSummary] = useState<string>("");
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [showResult, setShowResult] = useState(false);
  const [summaryResult, setSummaryResult] = useState("");
  const [typingText, setTypingText] = useState("");
  const dropdownRef = useRef<HTMLDivElement>(null);

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setSelectedFile(file);
    }
  };

  const handleDropdownSelect = (option: string) => {
    setSummaryType(option);
    setDropdownOpen(false);
  };

  // Close dropdown if clicked outside
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
        setDropdownOpen(false);
      }
    }
    if (dropdownOpen) {
      document.addEventListener("mousedown", handleClickOutside);
    } else {
      document.removeEventListener("mousedown", handleClickOutside);
    }
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [dropdownOpen]);

  const handleSubmit = async () => {
    if (selectedFile) {
      setShowResult(true); // Hide UI immediately
      try {
        // Get the full backend response
        const res = await uploadFile(selectedFile, summaryType.toUpperCase());
        // Show only result.data.summary
        setSummaryResult(res?.data?.summary);
        setTypingText("");
      } catch (err) {
        setSummaryResult("An error occurred while summarizing the document.");
        setTypingText("");
      }
    } else {
      // Optionally handle no file selected
    }
  };

  // Typing animation effect
  useEffect(() => {
    if (showResult && summaryResult) {
      setTypingText("");
      let i = 0;
      const interval = setInterval(() => {
        if (i < summaryResult.length) {
          setTypingText((prev) => prev + summaryResult[i]);
          i++;
        } else {
          clearInterval(interval);
        }
      }, 30);
      return () => clearInterval(interval);
    }
  }, [showResult, summaryResult]);

  const handleNewSummarization = () => {
    setShowResult(false);
    setSummaryResult("");
    setTypingText("");
    setSelectedFile(null);
    setSummaryType(SUMMARY_OPTIONS[0]);
    setCustomSummary("");
  };

  return (
    <>
      <Navbar />
      <div className="min-h-screen flex items-center justify-center">
        <div className="w-full max-w-3xl p-4">
          <div className="title text-3xl font-mono m-5 text-center">
            Hello. Welcome to AI Summarizer
          </div>

          {!showResult && (
            <>
              <div className="m-5 bg-gray-200 rounded-lg p-4 flex flex-col items-center justify-center border-2 border-dashed border-gray-400">
                <div className="text-left text-lg p-4">
                  {selectedFile ? selectedFile.name : "Drag or click to upload file"}
                </div>
                <input type="file" className="absolute opacity-0 cursor-pointer" onChange={handleFileChange} />
              </div>

              <div className="flex flex-row items-start gap-8 m-5">
                <div className="flex flex-col flex-1 relative" ref={dropdownRef}>
                  <label className="mb-2 font-mono text-lg">Summary Type</label>
                  <button
                    type="button"
                    className="border-2 border-gray-600 rounded px-4 py-2 text-base bg-gray-100 focus:outline-none focus:border-gray-800 shadow-lg w-full flex items-center justify-between cursor-pointer"
                    onClick={() => setDropdownOpen((open) => !open)}
                  >
                    <span>{summaryType}</span>
                    <svg className="w-5 h-5 ml-2 text-gray-700" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                    </svg>
                  </button>
                  {dropdownOpen && (
                    <div className="absolute left-0 right-0 mt-20 bg-white border border-gray-400 rounded-xl shadow-2xl z-50 py-2 transition-all duration-150">
                      {SUMMARY_OPTIONS.map(option => (
                        <button
                          key={option}
                          className={`block w-full text-left px-5 py-2 text-base rounded-lg transition-colors duration-100 ${option === summaryType ? 'bg-gray-200 font-semibold text-gray-900' : 'text-gray-700'} hover:bg-gray-100`}
                          onClick={() => handleDropdownSelect(option)}
                        >
                          {option}
                        </button>
                      ))}
                    </div>
                  )}
                </div>
                {summaryType === "Custom" && (
                  <div className="flex flex-col flex-1">
                    <label className="mb-2 font-mono text-lg">Custom Summary Instructions</label>
                    <textarea
                      className="min-h-32 p-4 w-full border-2 border-blue-400 focus:border-blue-600 rounded-lg"
                      placeholder="Describe your custom summary requirements..."
                      value={customSummary}
                      onChange={e => setCustomSummary(e.target.value)}
                    />
                  </div>
                )}
              </div>

              <div className="title text-3xl font-mono m-5 text-center">or</div>

              <div className="text-container m-5">
                <textarea placeholder="Paste your text here to summarize..." className="min-h-32 p-4 w-full border-2 border-gray-400 focus:border-2 focus:border-blue-200 rounded-lg" />
              </div>

              <div className="text-right p-0 m-5">
                <button
                  className="cursor-pointer m-0 p-0 border-2 border-black text-black bg-white-500 hover:bg-black hover:text-white py-2 px-4 rounded"
                  onClick={handleSubmit}
                >
                  Submit
                </button>
              </div>
            </>
          )}

          {showResult && (
            <>
              <div className="flex flex-col items-center justify-center min-h-80 p-10 bg-white border-2 border-gray-400 rounded-lg shadow-lg mb-4">
                <div className="font-mono text-lg mb-8 text-gray-700 w-full text-left" style={{ minHeight: 80 }}>
                  {typingText}
                  <span className="animate-pulse">|</span>
                </div>
              </div>
              <div className="flex justify-end">
                <button
                  className="px-6 py-2 rounded bg-black text-white font-mono text-base hover:bg-gray-800 shadow"
                  onClick={handleNewSummarization}
                >
                  New Summarization
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </>
  );
}

export default MainPage;