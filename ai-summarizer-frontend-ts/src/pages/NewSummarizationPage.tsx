import FileUploader from "../components/newsummarization/FileUploader";
import LoadingSpinner from "../components/newsummarization/LoadingSpinner";
import ResultPanel from "../components/newsummarization/ResultPanel";
import SummaryOptions from "../components/newsummarization/SummaryOptions";
import { useAuthStore } from "../store/authStore";
import { useState, useEffect, useRef } from "react";

const SUMMARY_OPTIONS = [
  "Brief",
  "Comprehensive",
  "Key_Points",
  "Executive",
  "Sentiment",
  "Technical",
  "Custom",
];

function NewSummarizationPage() {
  const uploadFile = useAuthStore((state) => state.uploadFile);
  const summarizeCustomText = useAuthStore(
    (state) => state.summarizeCustomText
  );
  const fetchProfile = useAuthStore((state) => state.fetchProfile);
  const profile = useAuthStore((state) => state.profile);
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [summaryType, setSummaryType] = useState<string>(SUMMARY_OPTIONS[0]);
  const [customSummary, setCustomSummary] = useState<string>("");
  const [customText, setCustomText] = useState<string>("");
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const [showResult, setShowResult] = useState(false);
  const [summaryResult, setSummaryResult] = useState("");
  const [typingText, setTypingText] = useState("");
  const [isTypingDone, setIsTypingDone] = useState(false);
  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  // Close dropdown if clicked outside
  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
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
      setIsTypingDone(false);
      setSummaryResult("");
      try {
        // Pass customSummary only if summaryType is 'Custom'
        const res = await uploadFile(
          selectedFile,
          summaryType.toUpperCase(),
          summaryType === "Custom" ? customSummary : undefined
        );
        setSummaryResult(
          typeof res?.data?.summary === "string" ? res.data.summary : ""
        );
        await fetchProfile();
      } catch (err) {
        setSummaryResult("An error occurred while summarizing the document.");
      }
    } else if (customText.trim()) {
      setShowResult(true);
      setIsTypingDone(false);
      setSummaryResult("");
      try {
        const userName = profile?.username || "";
        const type = summaryType.toUpperCase();
        const res = await summarizeCustomText({
          userName,
          type,
          customSummary: summaryType === "Custom" ? customSummary : undefined,
          customText,
        });
        setSummaryResult(
          typeof res?.data?.summary === "string" ? res.data.summary : ""
        );
        await fetchProfile();
      } catch (err) {
        setSummaryResult("An error occurred while summarizing the text.");
      }
      setCustomText("");
      setCustomSummary("");
    }
  };

  // Show result when summaryResult is set
  useEffect(() => {
    if (summaryResult) {
      setShowResult(true);
    }
  }, [summaryResult]);

  // Plain text typewriter effect, then markdown when done
  useEffect(() => {
    if (
      showResult &&
      typeof summaryResult === "string" &&
      summaryResult.length > 0
    ) {
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
              <div className="mb-2 text-lg font-bold text-left w-full">
                Upload file
              </div>
              <FileUploader
                selectedFile={selectedFile}
                onFileChange={setSelectedFile}
              />
              <div className="mb-4 text-sm text-gray-500 text-left">
                Supported files PPTX, DOCX, PDF, TXT
              </div>
              <div className="flex flex-row items-start gap-8 mb-5">
                <SummaryOptions
                  summaryType={summaryType}
                  onSummaryTypeChange={setSummaryType}
                  customSummary={customSummary}
                  onCustomSummaryChange={setCustomSummary}
                  dropdownOpen={dropdownOpen}
                  setDropdownOpen={setDropdownOpen}
                  dropdownRef={dropdownRef}
                  summaryOptions={SUMMARY_OPTIONS}
                />
                {summaryType === "Custom" && (
                  <div className="flex flex-col flex-1">
                    <label className="mb-2 text-lg font-bold">
                      Custom Summary Instructions
                    </label>
                    <textarea
                      className="min-h-32 p-4 w-full border border-blue-400 focus:border-blue-600 rounded"
                      style={{ fontFamily: "Segoe UI, Arial, sans-serif" }}
                      placeholder="Describe your custom summary requirements..."
                      value={customSummary}
                      onChange={(e) => setCustomSummary(e.target.value)}
                    />
                  </div>
                )}
              </div>
              <div className="title text-3xl mb-5 text-center">or</div>
              <div className="text-container mb-5">
                <textarea
                  placeholder="Paste your text here to summarize..."
                  className="min-h-32 p-4 w-full border-1 border-gray-400 focus:border-2 focus:border-blue-200 rounded"
                  style={{ fontFamily: "Segoe UI, Arial, sans-serif" }}
                  value={customText}
                  onChange={(e) => setCustomText(e.target.value)}
                />
              </div>

              <div className="flex justify-end p-0 mb-5">
                <button
                  className="text-sm cursor-pointer m-0 p-0 text-white bg-green-700 hover:bg-green-900 hover:text-white py-1.5 px-7 rounded flex items-center justify-center gap-2"
                  style={{ fontFamily: "Segoe UI, Arial, sans-serif" }}
                  onClick={handleSubmit}
                >
                  Submit
                </button>
              </div>
            </>
          )}
          {showResult && !summaryResult && (
            <>
              <div className="text-xl font-bold text-left mb-2">Summary</div>
              <LoadingSpinner />
            </>
          )}
          {showResult && summaryResult && (
            <>
              <ResultPanel
                summaryResult={summaryResult}
                typingText={typingText}
                isTypingDone={isTypingDone}
                onCreateNew={handleNewSummarization}
              />
            </>
          )}
        </div>
      </div>
    </>
  );
}

export default NewSummarizationPage;
