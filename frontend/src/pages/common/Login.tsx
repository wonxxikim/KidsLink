import React, { useState } from "react";
import LoginHeader from "../../components/login/LoginHeader";
import mainImg from "../../assets/teacher/main_img.png";
import { Link, useNavigate } from "react-router-dom";
import { login as loginAPI } from "../../api/member";
import { useAppStore } from "../../stores/store"; // Update import here

export default function Login() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string | null>(null);
  const navigate = useNavigate();
  const setUserType = useAppStore((state) => state.setUserType); // Update here

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const data = await loginAPI({ username, password });
      console.log("Login successful:", data);
      setUserType(data.role); // userType을 Zustand 스토어에 저장
      navigate("/"); // 로그인 성공 후 이동할 경로
    } catch (error) {
      console.error("Error during login:", error);
      setError("로그인에 실패했습니다. 다시 시도해주세요.");
    }
  };

  return (
    <>
      <LoginHeader />
      <div className="flex flex-row bg-[#fff9d7] min-h-[calc(100vh-85px)] h-full font-KoPubDotum z-1">
        <div className="mx-[150px] py-[100px]">
          <p className="text-[38px] font-bold text-left text-[#363636] mb-[65px]">
            소중한 추억을 기록하며,
            <br />
            교육의 모든 순간을 함께하세요.
          </p>

          <form className="flex flex-col" onSubmit={handleLogin}>
            <input
              type="text"
              placeholder="로그인"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              className="w-[450px] h-[62px] rounded-[20px] p-5 bg-white border border-[#b9b9b9] mb-3 text-[22px] font-medium text-left text-[#b9b9b9]"
            />
            <input
              type="password"
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="w-[450px] h-[62px] rounded-[20px] p-5 bg-white border border-[#b9b9b9] mb-5 text-[22px] font-medium text-left text-[#b9b9b9]"
            />
            <button
              type="submit"
              className="w-[450px] h-[62px] rounded-[20px] bg-[#ffe96f] text-[24px] font-bold mb-3"
            >
              로그인
            </button>
          </form>
          {error && (
            <p className="text-[22px] font-medium text-left text-red-500">
              {error}
            </p>
          )}
          <Link to="/join">
            <p className="text-[22px] font-medium text-left text-[#363636]">
              키즈링크가 처음이라면?
            </p>
          </Link>
        </div>

        <img
          src={mainImg}
          className="w-[630px] absolute left-[650px] top-[180px] object-cover"
        />
      </div>
    </>
  );
}