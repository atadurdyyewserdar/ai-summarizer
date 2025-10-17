import { useState } from 'react'

function MainPage() {
  const [count, setCount] = useState(0)

  return (
    <>
      <div className='max-w-200 container mx-auto p-4 flex flex-col items-center justify-center items-center h-screen w-500'>
        <div className="title text-3xl font-mono m-5 w-200">
            Hello. Welcome to AI Summarizer
        </div>
        <div className="m-5 max-w-200 bg-gray-200 rounded-lg p-10 flex flex-col items-center justify-center border-2 border-dashed border-gray-400 w-200 h-20">
          <div className="text-left w-200 text-lg p-10">
            Drag or click to upload file
          </div>
          <input type="file" className='absolute opacity-0 w-200 h-20 cursor-pointer'/>
        </div>
        <div className="title text-3xl font-mono m-5 w-200">
            or
        </div>
        <div className="text-container max-w-500 m-5">
          <textarea placeholder='Paste your text here to summarize...' className='min-h-70 p-4 w-200 border-2 border-gray-400 focus:border-2 focus:border-blue-200 rounded-lg'/>       </div>
        <div className="text-right max-w-500 w-200 p-0 m-5">
          <button className='cursor-pointer m-0 p-0 border-2 border-black text-black bg-white-500 hover:bg-black hover:text-white py-2 px-4 rounded'>Submit</button>
        </div>
        
      </div>
    </>
  )
}

export default MainPage
