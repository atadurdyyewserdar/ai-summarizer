import { useState } from "react";

function SignInPage() {
  const [count, setCount] = useState(0);

  return (
    <>
      <div className="max-w-200 container mx-auto p-4 flex flex-col items-center justify-center items-center h-screen w-500">
        <div className="title text-xl font-mono m-3 w-100">Please Sign In</div>
        <input
          placeholder="Login"
          type="text"
          className="text-sm italic w-100 h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
        />
        <input
          placeholder="Password"
          type="text"
          className="text-sm italic w-100 h-12 border-2 border-gray-600 rounded-lg m-3 p-5"
        />
        <div className="w-100 m-3 text-right">
          <button className="mx-3 hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 pl-5 pr-5 rounded">
            Sign up
          </button>
          <button className="hover:border-gray-800 text-center text-sm align-center cursor-pointer border-2 border-gray-600 text-black bg-white-500 hover:bg-gray-800 hover:text-white h-10 pl-5 pr-5 rounded">
            Sign in
          </button>
        </div>
      </div>
    </>
  );
}

export default SignInPage;
