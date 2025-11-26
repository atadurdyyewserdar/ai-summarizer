import { useNavigate, useParams } from "react-router-dom";
import { Navbar } from "../components/Navbar";
import { useAuthStore } from "../store/authStore";
import type { SummarizationItem } from "../store/authStore";
import { useEffect } from "react";
import fileIcon from "../assets/file.png";
import NewSummarizationPage from "./NewSummarizationPage";


const getFileIcon = () => fileIcon;

export const SummarrizationPage = () => {
  const navigate = useNavigate();
  const { summarizationId } = useParams<{ summarizationId?: string }>();
  const fetchProfile = useAuthStore((s) => s.fetchProfile);
  const profile = useAuthStore((s) => s.profile);
  const loadingProfile = useAuthStore((s) => s.loadingProfile);
  const profileError = useAuthStore((s) => s.profileError);
  const summarizationList: SummarizationItem[] = profile?.summarizationHistoryList || [];
  const selected = summarizationList.find((item) => item.id === summarizationId);

  useEffect(() => {
    fetchProfile();
    // eslint-disable-next-line
  }, []);

  return (
    <div className="min-h-screen bg-white">
      <Navbar />
      <div className="flex justify-center items-start min-h-[calc(100vh-80px)]">
        <div className="w-full max-w-[1200px] mx-auto bg-white border border-gray-300 rounded-lg flex min-h-[400px]" style={{ marginTop: 32, height: 'calc(100vh - 120px)' }}>
          {/* Left: List */}
          <div className="w-1/3 border-r border-gray-200 p-4 overflow-y-auto">
            <div className="mb-4">
              <button
                className="bg-black text-white w-full py-2 rounded hover:bg-gray-900 text-base cursor-pointer text-center"
                style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}
                onClick={() => {
                  navigate('/summarization');
                }}
              >
                New File
              </button>
            </div>
            {loadingProfile && <div className="text-gray-400 text-center my-4">Loading...</div>}
            {profileError && <div className="text-red-500 text-center my-4">{profileError}</div>}
            {summarizationList.map((item) => (
              <div
                key={item.id}
                className={`flex items-center gap-2 p-2 mb-2 rounded cursor-pointer transition-all ${item.id === summarizationId
                  ? "bg-gray-200 border-l-4 border-blue-500 font-medium"
                  : "hover:bg-gray-100"}`}
                onClick={() => {
                  navigate(`/summarization/${item.id}`);
                }}
              >
                    <img src={getFileIcon()} alt="file icon" className="w-5 h-5 object-contain" />
                <span className="text-base text-gray-800" style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}>{item.fileName}</span>
              </div>
            ))}
          </div>
          {/* Right: Summary or New Summarization */}
          <div className="w-2/3 p-3 flex flex-col h-full">
            {summarizationId === undefined ? (
              <NewSummarizationPage />
            ) : selected ? (
              <div className="flex flex-col flex-1 h-full">
                <div>
                  <div className="text-lg font-semibold mb-2 w-full text-left">Summary</div>
                  <div className="text-gray-700 text-base whitespace-pre-line break-words w-full text-left mb-4" style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}>
                    {selected.summaryText || selected.inputText}
                  </div>
                </div>
                <div className="mt-auto">
                  <div className="grid grid-cols-2 gap-4 text-sm text-black bg-gray-50 border border-gray-200 rounded-lg p-4 w-full" style={{ fontFamily: 'Segoe UI, Arial, sans-serif' }}>
                    {selected.summaryType !== undefined && (
                      <div><span className="font-semibold">Summary Type:</span> {selected.summaryType}</div>
                    )}
                    {selected.imageCount !== undefined && (
                      <div><span className="font-semibold">Images:</span> {selected.imageCount}</div>
                    )}
                    {selected.paragraphCount !== undefined && (
                      <div><span className="font-semibold">Paragraphs:</span> {selected.paragraphCount}</div>
                    )}
                    {selected.slideCount !== undefined && (
                      <div><span className="font-semibold">Slides:</span> {selected.slideCount}</div>
                    )}
                    {selected.processingTime !== undefined && (
                      <div><span className="font-semibold">Processing Time:</span> {(selected.processingTime / 1000).toFixed(2)} s</div>
                    )}
                    {selected.tableCount !== undefined && (
                      <div><span className="font-semibold">Tables:</span> {selected.tableCount}</div>
                    )}
                    {selected.wordCount !== undefined && (
                      <div><span className="font-semibold">Words:</span> {selected.wordCount}</div>
                    )}
                    {selected.fileSize !== undefined && (
                      <div><span className="font-semibold">File Size:</span> {(selected.fileSize / (1024 * 1024)).toFixed(2)} MB</div>
                    )}
                  </div>
                </div>
              </div>
            ) : (
              <div className="text-gray-400 text-lg font-mono flex flex-col items-center justify-center h-full">
                <div className="mb-4">Select summarization from list or create new one.</div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};
