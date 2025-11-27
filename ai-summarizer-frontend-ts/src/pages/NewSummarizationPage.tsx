import { useAuthStore } from "../store/authStore";
import { useState, useEffect, useRef } from "react";

// Animated loader CSS
const loaderStyle = `
.loader {
  width: fit-content;
  font-size: 17px;
  font-family: monospace;
  line-height: 1.4;
  font-weight: bold;
  --c: no-repeat linear-gradient(#000 0 0); 
  background: var(--c),var(--c),var(--c),var(--c),var(--c),var(--c),var(--c);
  background-size: calc(1ch + 1px) 100%;
  border-bottom: 10px solid #0000; 
  position: relative;
  animation: l8-0 3s infinite linear;
  clip-path: inset(-20px 0);
}
.loader::before {
  content:"Loading";
}
.loader::after {
  content: "";
  position: absolute;
  width: 10px;
  height: 14px;
  background: #25adda;
  left: -10px;
  bottom: 100%;
  animation: l8-1 3s infinite linear;
}
@keyframes l8-0{
   0%,
   12.5% {background-position: calc(0*100%/6) 0   ,calc(1*100%/6)    0,calc(2*100%/6)    0,calc(3*100%/6)    0,calc(4*100%/6)    0,calc(5*100%/6)    0,calc(6*100%/6) 0}
   25%   {background-position: calc(0*100%/6) 40px,calc(1*100%/6)    0,calc(2*100%/6)    0,calc(3*100%/6)    0,calc(4*100%/6)    0,calc(5*100%/6)    0,calc(6*100%/6) 0}
   37.5% {background-position: calc(0*100%/6) 40px,calc(1*100%/6) 40px,calc(2*100%/6)    0,calc(3*100%/6)    0,calc(4*100%/6)    0,calc(5*100%/6)    0,calc(6*100%/6) 0}
   50%   {background-position: calc(0*100%/6) 40px,calc(1*100%/6) 40px,calc(2*100%/6) 40px,calc(3*100%/6)    0,calc(4*100%/6)    0,calc(5*100%/6)    0,calc(6*100%/6) 0}
   62.5% {background-position: calc(0*100%/6) 40px,calc(1*100%/6) 40px,calc(2*100%/6) 40px,calc(3*100%/6) 40px,calc(4*100%/6)    0,calc(5*100%/6)    0,calc(6*100%/6) 0}
   75%   {background-position: calc(0*100%/6) 40px,calc(1*100%/6) 40px,calc(2*100%/6) 40px,calc(3*100%/6) 40px,calc(4*100%/6) 40px,calc(5*100%/6)    0,calc(6*100%/6) 0}
   87.4% {background-position: calc(0*100%/6) 40px,calc(1*100%/6) 40px,calc(2*100%/6) 40px,calc(3*100%/6) 40px,calc(4*100%/6) 40px,calc(5*100%/6) 40px,calc(6*100%/6) 0}
   100%  {background-position: calc(0*100%/6) 40px,calc(1*100%/6) 40px,calc(2*100%/6) 40px,calc(3*100%/6) 40px,calc(4*100%/6) 40px,calc(5*100%/6) 40px,calc(6*100%/6) 40px}
}
@keyframes l8-1{
  100% {left:115%}
}
`;

const SUMMARY_OPTIONS = [
  "Brief",
  "Comprehensive",
  "Key Points",
  "Executive",
  "Sentiment",
  "Technical",
  "Custom",
];

function NewSummarizationPage() {
  const uploadFile = useAuthStore((state) => state.uploadFile);
  const fetchProfile = useAuthStore((state) => state.fetchProfile);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [summaryType, setSummaryType] = useState<string>(SUMMARY_OPTIONS[0]);
  const [customSummary, setCustomSummary] = useState<string>("");
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [showResult, setShowResult] = useState(false);
  const [summaryResult, setSummaryResult] = useState("");
  const [typingText, setTypingText] = useState("");
  const [isTypingDone, setIsTypingDone] = useState(false);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
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
      setShowResult(true); // Show typewriter area immediately
      setTypingText("Summarizing...");
      setIsTypingDone(false);
      setSummaryResult("");
      try {
        // Pass customSummary only if summaryType is 'Custom'
        const res = await uploadFile(
          selectedFile,
          summaryType.toUpperCase(),
          summaryType === "Custom" ? customSummary : undefined
        );
        // Show only result.data.summary, ensure it's a string
        setSummaryResult(typeof res?.data?.summary === "string" ? res.data.summary : "");
        setTypingText("");
        // Refresh profile to update summarization history
        await fetchProfile();
        // setShowResult(true) will be handled by useEffect below
      } catch (err) {
        setSummaryResult("An error occurred while summarizing the document.");
        setTypingText("");
        // setShowResult(true) will be handled by useEffect below
      }
    } else {
      // Optionally handle no file selected
    }
  };

  // Show result when summaryResult is set
  useEffect(() => {
    if (summaryResult) {
      setShowResult(true);
    }
  }, [summaryResult]);

  // Plain text typewriter effect, then show markdown when done
  useEffect(() => {
    if (showResult && typeof summaryResult === 'string' && summaryResult.length > 0) {
      setTypingText(""); 
      setIsTypingDone(false);
      let i = 0;
      const safeSummary = summaryResult;
      if (intervalRef.current) clearInterval(intervalRef.current);
      intervalRef.current = setInterval(() => {
        if (i < safeSummary.length) {
          setTypingText(safeSummary.slice(0, i + 1));
          i++;
        } else {
          setIsTypingDone(true);
          if (intervalRef.current) clearInterval(intervalRef.current);
        }
      }, 2);
      return () => {
        if (intervalRef.current) clearInterval(intervalRef.current);
      };
    }
  }, [showResult, summaryResult]);

  const handleNewSummarization = () => {
    setShowResult(false);
    setSummaryResult("");
    setTypingText("");
    setIsTypingDone(false);
    setSelectedFile(null);
    setSummaryType(SUMMARY_OPTIONS[0]);
    setCustomSummary("");
  };

  return (
    <>
      <div className="min-h-screen flex justify-center">
        <div className="w-full max-w-3xl p-4">
          {!showResult && (
            <>
              <div className="mb-2 text-lg font-bold text-left w-full">Upload file</div>
              <label className="mb-2 bg-gray-200 rounded-lg p-4 flex flex-col items-center justify-center border-2 border-dashed border-gray-400 cursor-pointer relative">
                <div className="text-center text-lg p-4 w-full">
                  {selectedFile ? selectedFile.name : "Drag or click to upload file"}
                </div>
                <input type="file" className="absolute inset-0 w-full h-full opacity-0 cursor-pointer" onChange={handleFileChange} tabIndex={-1} />
              </label>
              <div className="mb-4 text-sm text-gray-500 text-left">Supported files PPTX, DOCX, PDF, TXT</div>

              <div className="flex flex-row items-start gap-8 mb-5">
                <div className="flex flex-col flex-1 relative" ref={dropdownRef}>
                  <label htmlFor="summaryType" className="block mb-1 font-semibold text-lg">
                    Summary type
                  </label>
                  <button
                    type="button"
                    className="border border-gray-600 rounded px-3 py-2 text-base bg-gray-100 focus:outline-none focus:border-gray-800 flex items-center justify-between cursor-pointer"
                    style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}
                    onClick={() => setDropdownOpen((open) => !open)}
                  >
                    <span>{summaryType}</span>
                    <svg className="w-5 h-5 ml-2 text-gray-700" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" d="M19 9l-7 7-7-7" />
                    </svg>
                  </button>
                  {dropdownOpen && (
                    <div className="absolute left-0 right-0 mt-20 bg-gray-50 border border-gray-400 rounded z-50 py-1 transition-all duration-150">
                      {SUMMARY_OPTIONS.map(option => (
                        <button
                          key={option}
                          className={`block w-full text-left px-4 py-2 text-base rounded transition-colors duration-100 cursor-pointer bg-gray-50 ${option === summaryType ? 'bg-gray-200 font-semibold text-gray-900' : 'text-gray-700'} hover:bg-gray-100`}
                          style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}
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
                    <label className="mb-2 text-lg font-bold">Custom Summary Instructions</label>
                    <textarea
                      className="min-h-32 p-4 w-full border border-blue-400 focus:border-blue-600 rounded"
                      style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}
                      placeholder="Describe your custom summary requirements..."
                      value={customSummary}
                      onChange={e => setCustomSummary(e.target.value)}
                    />
                  </div>
                )}
              </div>

              <div className="title text-3xl mb-5 text-center">or</div>

              <div className="text-container mb-5">
                <textarea placeholder="Paste your text here to summarize..." className="min-h-32 p-4 w-full border-1 border-gray-400 focus:border-2 focus:border-blue-200 rounded" style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }} />
              </div>

              <div className="flex justify-end p-0 mb-5">
                <button
                  className="text-sm cursor-pointer m-0 p-0 text-white bg-green-700 hover:bg-green-900 hover:text-white py-1.5 px-7 rounded flex items-center justify-center gap-2"
                  style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}
                  onClick={handleSubmit}
                >
                  Submit
                </button>
              </div>
            </>
          )}

          {/* Show loading GIF while waiting for backend, then show typewriter/result */}
          {showResult && !summaryResult && (
            <div className="flex flex-col items-center justify-center min-h-80 bg-white border border-gray-400 rounded mb-4">
              <style>{loaderStyle}</style>
              <div className="loader" />
            </div>
          )}
          {showResult && summaryResult && (
            <>
              <div className="justify-center min-h-80 bg-white border border-gray-400 rounded mb-4">
                <div className="p-5 text-lg mb-8 text-gray-700 w-full text-left" style={{ minHeight: 80, fontFamily: 'Segoe UI, Arial, sans-serif' }}>
                  {!isTypingDone ? (
                    <pre style={{whiteSpace: 'pre-wrap', wordBreak: 'break-word', margin: 0, fontFamily: 'Segoe UI, Arial, sans-serif', fontSize: '1rem'}}>{typingText}<span className="animate-pulse">|</span></pre>
                  ) : (
                    <pre style={{whiteSpace: 'pre-wrap', wordBreak: 'break-word', margin: 0, fontFamily: 'Segoe UI, Arial, sans-serif', fontSize: '1rem'}}>{summaryResult}</pre>
                  )}
                </div>
              </div>
              <div className="flex justify-end">
                <button
                  className="px-5 py-1.5 rounded bg-green-700 text-white text-sm shadow cursor-pointer transition-colors duration-200 hover:bg-green-900"
                  style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}
                  onClick={handleNewSummarization}
                >
                  Create new
                </button>
              </div>
            </>
          )}
        </div>
      </div>
    </>
  );
}

export default NewSummarizationPage;
