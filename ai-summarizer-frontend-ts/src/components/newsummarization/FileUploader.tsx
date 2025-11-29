import React from "react";

interface FileUploaderProps {
  selectedFile: File | null;
  onFileChange: (file: File | null) => void;
}

const FileUploader: React.FC<FileUploaderProps> = ({
  selectedFile,
  onFileChange,
}) => {
  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      onFileChange(file);
    }
  };

  return (
    <label className="mb-2 bg-gray-200 rounded-lg p-4 flex flex-col items-center justify-center border-2 border-dashed border-gray-400 cursor-pointer relative">
      <div className="text-center text-lg p-4 w-full">
        {selectedFile ? selectedFile.name : "Drag or click to upload file"}
      </div>
      <input
        type="file"
        className="absolute inset-0 w-full h-full opacity-0 cursor-pointer"
        onChange={(e) => handleFileChange(e)}
        tabIndex={-1}
      />
    </label>
  );
};

export default FileUploader;
