import { Navbar } from "../components/Navbar";
import { useEffect, useState, useRef } from "react";
import { useAuthStore } from "../store/authStore";
import { useNavigate } from "react-router-dom";

function MainPage() {
    const isAuthenticated = useAuthStore((s) => s.isAuthenticated);
    const navigate = useNavigate();
  const introText =
    "Tired of reading long documents or sitting through endless slides? Our AI-powered summarizer is here to help you save time and energy. Just upload your files or paste any text, choose the summary style that fits your needs, and get a clear, easy-to-read summary in seconds. Whether you’re a student, professional, or just curious, you’ll love how fast and simple it is to get the key points without the hassle. No signup required, always free, and your privacy is our priority.";

  const [typedText, setTypedText] = useState("");

  const indexRef = useRef(0);
  useEffect(() => {
    let timeout: ReturnType<typeof setTimeout>;
    function type() {
      setTypedText(introText.slice(0, indexRef.current));
      if (indexRef.current < introText.length) {
        indexRef.current++;
        timeout = setTimeout(type, 8); // Fast typing
      } else {
        timeout = setTimeout(() => {
          indexRef.current = 0;
          setTypedText("");
          setTimeout(type, 300); // Short pause before restart
        }, 1200);
      }
    }
    type();
    return () => clearTimeout(timeout);
    // eslint-disable-next-line
  }, []);

  return (
    <>
      <Navbar />
      <div className="w-full flex flex-col items-center pt-8">
        <div
          className="w-full"
          style={{
            maxWidth: "1200px",
            paddingLeft: "0.5rem",
            paddingRight: "0.5rem",
          }}
        >
          <div
            className="flex flex-row bg-white rounded-2xl mx-0"
            style={{ alignItems: "stretch", width: "1200px" }}
          >
            {/* Left: Animated Logo */}
            <div className="flex flex-col items-center justify-center w-[600px] min-w-[300px] max-w-[600px] h-[40rem] pl-0 pr-8">
              <svg
                viewBox="10 10 110 310"
                fill="none"
                xmlns="http://www.w3.org/2000/svg"
                className="drop-shadow-2xl"
                style={{
                  width: "36rem",
                  height: "36rem",
                  minWidth: "36rem",
                  minHeight: "36rem",
                }}
              >
                {/* Glowing base */}
                <ellipse
                  cx="90"
                  cy="165"
                  rx="50"
                  ry="14"
                  fill="#3B82F6"
                  opacity="0.12"
                >
                  <animate
                    attributeName="opacity"
                    values="0.12;0.3;0.12"
                    dur="2s"
                    repeatCount="indefinite"
                  />
                </ellipse>
                {/* Central morphing crystal */}
                <g className="crystal-morph">
                  <polygon
                    points="90,40 130,90 90,140 50,90"
                    fill="url(#gradient1)"
                    opacity="0.9"
                  />
                  <polygon
                    points="90,50 120,90 90,130 60,90"
                    fill="url(#gradient2)"
                    opacity="0.8"
                  />
                  <polygon
                    points="90,60 110,90 90,120 70,90"
                    fill="#fff"
                    opacity="0.95"
                  />
                </g>
                {/* Rotating outer rings */}
                <g className="ring1">
                  <circle
                    cx="90"
                    cy="90"
                    r="65"
                    stroke="#3B82F6"
                    strokeWidth="2"
                    opacity="0.3"
                    strokeDasharray="10 15"
                  />
                </g>
                <g className="ring2">
                  <circle
                    cx="90"
                    cy="90"
                    r="52"
                    stroke="#6366F1"
                    strokeWidth="2.5"
                    opacity="0.4"
                    strokeDasharray="8 12"
                  />
                </g>
                {/* Floating document icons */}
                {/* PDF - Top right */}
                <g className="doc-float1">
                  <rect
                    x="134"
                    y="52"
                    width="20"
                    height="26"
                    rx="3"
                    fill="#EF4444"
                    opacity="0.9"
                  />
                  <text
                    x="144"
                    y="68"
                    fontSize="8"
                    fill="white"
                    fontWeight="bold"
                    textAnchor="middle"
                  >
                    PDF
                  </text>
                </g>
                {/* WORD - Bottom left */}
                <g className="doc-float2">
                  <rect
                    x="26"
                    y="112"
                    width="20"
                    height="26"
                    rx="3"
                    fill="#2563EB"
                    opacity="0.9"
                  />
                  <text
                    x="36"
                    y="128"
                    fontSize="7"
                    fill="white"
                    fontWeight="bold"
                    textAnchor="middle"
                  >
                    DOC
                  </text>
                </g>
                {/* PPT - Bottom right */}
                <g className="doc-float3">
                  <rect
                    x="134"
                    y="112"
                    width="20"
                    height="26"
                    rx="3"
                    fill="#F59E0B"
                    opacity="0.9"
                  />
                  <text
                    x="144"
                    y="128"
                    fontSize="7"
                    fill="white"
                    fontWeight="bold"
                    textAnchor="middle"
                  >
                    PPT
                  </text>
                </g>
                {/* TXT - Top left */}
                <g className="doc-float4">
                  <rect
                    x="26"
                    y="52"
                    width="20"
                    height="26"
                    rx="3"
                    fill="#10B981"
                    opacity="0.9"
                  />
                  <text
                    x="36"
                    y="68"
                    fontSize="8"
                    fill="white"
                    fontWeight="bold"
                    textAnchor="middle"
                  >
                    TXT
                  </text>
                </g>
                {/* Inner energy lines */}
                <line
                  className="energy1"
                  x1="90"
                  y1="75"
                  x2="90"
                  y2="105"
                  stroke="#FACC15"
                  strokeWidth="2"
                  strokeLinecap="round"
                />
                <line
                  className="energy2"
                  x1="75"
                  y1="90"
                  x2="105"
                  y2="90"
                  stroke="#34D399"
                  strokeWidth="2"
                  strokeLinecap="round"
                />
                {/* Gradients */}
                <defs>
                  <linearGradient
                    id="gradient1"
                    x1="0%"
                    y1="0%"
                    x2="100%"
                    y2="100%"
                  >
                    <stop offset="0%" stopColor="#3B82F6" />
                    <stop offset="100%" stopColor="#6366F1" />
                  </linearGradient>
                  <linearGradient
                    id="gradient2"
                    x1="0%"
                    y1="0%"
                    x2="100%"
                    y2="100%"
                  >
                    <stop offset="0%" stopColor="#6366F1" />
                    <stop offset="100%" stopColor="#8B5CF6" />
                  </linearGradient>
                </defs>
              </svg>
              <style>{`
                  .crystal-morph { animation: morph 3s ease-in-out infinite alternate; transform-origin: 90px 90px; }
                  @keyframes morph { 
                    0% { transform: rotate(0deg) scale(1); }
                    50% { transform: rotate(180deg) scale(1.08); }
                    100% { transform: rotate(360deg) scale(1); }
                  }
                  .ring1 { transform-origin: 90px 90px; animation: ring-rotate1 8s linear infinite; }
                  .ring2 { transform-origin: 90px 90px; animation: ring-rotate2 6s linear infinite reverse; }
                  @keyframes ring-rotate1 { 100% { transform: rotate(360deg); } }
                  @keyframes ring-rotate2 { 100% { transform: rotate(360deg); } }
                  .doc-float1 { animation: doc-float1 2.5s ease-in-out infinite alternate; }
                  .doc-float2 { animation: doc-float2 2.8s ease-in-out infinite alternate; }
                  .doc-float3 { animation: doc-float3 3.1s ease-in-out infinite alternate; }
                  .doc-float4 { animation: doc-float4 2.6s ease-in-out infinite alternate; }
                  @keyframes doc-float1 { 0% { transform: translate(0, 0) rotate(0deg); opacity: 0.9; } 100% { transform: translate(-8px, -12px) rotate(-10deg); opacity: 1; } }
                  @keyframes doc-float2 { 0% { transform: translate(0, 0) rotate(0deg); opacity: 0.9; } 100% { transform: translate(10px, 8px) rotate(8deg); opacity: 1; } }
                  @keyframes doc-float3 { 0% { transform: translate(0, 0) rotate(0deg); opacity: 0.9; } 100% { transform: translate(-10px, 10px) rotate(-8deg); opacity: 1; } }
                  @keyframes doc-float4 { 0% { transform: translate(0, 0) rotate(0deg); opacity: 0.9; } 100% { transform: translate(12px, -10px) rotate(10deg); opacity: 1; } }
                  .energy1 { animation: energy-pulse1 1.5s ease-in-out infinite alternate; }
                  .energy2 { animation: energy-pulse2 1.8s ease-in-out infinite alternate; }
                  @keyframes energy-pulse1 { 0% { opacity: 0.3; stroke-width: 1; } 100% { opacity: 1; stroke-width: 3; } }
                  @keyframes energy-pulse2 { 0% { opacity: 0.3; stroke-width: 1; } 100% { opacity: 1; stroke-width: 3; } }
                `}</style>
              {/* Animated geometric crystal with document icons (max size, no text) */}
            </div>
            {/* Right: Intro Text */}
            <div className="flex flex-col h-full text-left w-[600px] min-w-[300px] max-w-[600px] p-8 mb-8" style={{ minHeight: '40rem' }}>
              <div>
                <div className="text-3xl font-bold mb-4 text-black">
                  Summarize Anything Instantly
                </div>
                <div className="text-lg text-gray-600 mb-6" style={{ minHeight: '305px', maxHeight: '305px', overflow: 'hidden', fontFamily: 'Segoe UI, Arial, sans-serif' }}>
                  {typedText}
                  <span className="animate-pulse">|</span>
                </div>
              </div>
              <div className="flex-1" />
              <div className="w-full mb-40">
                <button
                  type="button"
                  className="relative overflow-hidden text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 h-12 px-24 rounded transition-colors duration-200 group min-w-[340px] max-w-[480px]"
                  style={{ fontWeight: 600, fontSize: '1.15rem', display: 'block', fontFamily: 'Segoe UI, Arial, sans-serif' }}
                  onClick={() => {
                    if (isAuthenticated) {
                      navigate("/summarization");
                    } else {
                      navigate("/signin");
                    }
                  }}
                >
                  <span className="relative z-10 group-hover:text-white transition-colors duration-200">Start Now</span>
                  <span className="absolute left-0 top-0 h-full w-0 group-hover:w-full bg-black transition-all duration-300 ease-out z-0" style={{transitionProperty: 'width'}}></span>
                </button>
                <style>{`
                  .group:hover .group-hover\\:w-full { width: 100% !important; }
                  .group .group-hover\\:w-full { transition-property: width; }
                `}</style>
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}

export default MainPage;
