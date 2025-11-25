
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { Navbar } from "../components/Navbar";
import pptIcon from "../assets/powerpoint.png";
import wordIcon from "../assets/word.png";
import txtIcon from "../assets/txt.png";
import pdfIcon from "../assets/pdf.png";

const getFileIcon = (type: string) => {
  switch (type) {
    case "docx":
      return wordIcon;
    case "pdf":
      return pdfIcon;
    case "txt":
      return txtIcon;
    case "pptx":
      return pptIcon;
    default:
      return txtIcon;
  }
};

export const SummarrizationPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const { summarizationId } = useParams();
  const { summarizationList = [], selectedId } = location.state || {};
  const selected = summarizationList.find((item: any) => item.id === (summarizationId || selectedId));

  return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />
         <div className="flex justify-center items-start min-h-[calc(100vh-80px)]">
           <div className="w-full max-w-[1200px] mx-auto bg-white border-1 border-gray-300 rounded-lg shadow-sm flex min-h-[400px]" style={{marginTop: 32}}>
          {/* Left: List */}
          <div className="w-1/3 border-r border-gray-200 p-4 overflow-y-auto">
            {summarizationList.map((item: any) => (
              <div
                key={item.id}
                className={`flex items-center gap-2 p-2 mb-2 rounded cursor-pointer transition-all ${item.id === (summarizationId || selectedId)
                  ? "bg-gray-200 border-l-4 border-blue-500 font-bold"
                  : "hover:bg-gray-100"}`}
                onClick={() => navigate(`/summarization/${item.id}`, { state: { summarizationList, selectedId: item.id } })}
              >
                <img src={getFileIcon(item.documentType)} alt={item.documentType + " icon"} className="w-7 h-7 object-contain" />
                <span className="text-base font-mono text-gray-800">{item.fileName}</span>
              </div>
            ))}
          </div>
          {/* Right: Summary */}
          <div className="w-2/3 p-6 flex flex-col">
            {selected ? (
              <>
                <div className="text-lg font-semibold mb-2 w-full text-left">Summary</div>
                <div className="text-gray-700 font-mono text-base whitespace-pre-line break-words w-full text-left">
                  {selected.summaryText || selected.inputText}
                </div>
              </>
            ) : (
              <div className="text-gray-400">No summary selected.</div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
