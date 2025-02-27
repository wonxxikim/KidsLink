import Title from "../../components/teacher/common/Title";
import { useState, useEffect } from "react";
import { FiUpload } from "react-icons/fi";
import { FaMinusCircle } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import PulseLoader from "react-spinners/PulseLoader";
import { handleClassify, handleDeleteImage, handleImageUpload } from "../../utils/album";
import TeacherLayout from "../../layouts/TeacherLayout";
import daramgi from "../../assets/teacher/camera-daramgi.png";
import ThreeModel from "../../components/ThreeModel2";
import { Vector2 } from "three";

export default function TeacherAlbum() {
  const navigate = useNavigate();
  const [images, setImages] = useState<File[]>([]);
  const [loading, setLoading] = useState(false);
  const [loadingMessage, setLoadingMessage] = useState("사진을 분류하는 중입니다...");

  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (loading) {
      interval = setInterval(() => {
        setLoadingMessage(prev => prev === "사진을 분류하는 중입니다..." ? "조금만 기다려주세요..." : "사진을 분류하는 중입니다...");
      }, 4000);
    }
    return () => clearInterval(interval);
  }, [loading]);

  const tabs = [
    { label: "사진분류", link: "/album" },
    { label: "전송내역", link: "/album/history" },
];

const [mousePosition, setMousePosition] = useState(new Vector2(0, 0));
useEffect(() => {
  const handleMouseMove = (event: MouseEvent) => {
    setMousePosition(new Vector2(event.clientX, event.clientY));
  };
  window.addEventListener('mousemove', handleMouseMove);
  return () => window.removeEventListener('mousemove', handleMouseMove);
}, []);

  return (
    <TeacherLayout
        activeMenu="album"
        setActiveMenu={() => {}}
        titleComponent={<Title 
          title="사진분류" 
          tooltipContent={
            <div className="leading-relaxed w-[240px]">
              업로드한 사진을 AI가 아이별로 분류하고, 해당 아이의 학부모에게 전송합니다. 정상적으로 분류되지 않은 사진은 수동으로 분류할 수 있으니 안심하세요!
            </div>
          }
          tabs={tabs} 
        />}
        imageSrc={daramgi} 
    >
      <div className="fixed z-30 flex gap-12">

        
        {/* <ThreeModel mousePosition={mousePosition} /> */}
        
      </div>
      <div className="relative w-full lg:my-10 my-5 mb-32 px-7 flex flex-col items-center">
        <div className={`h-[470px] flex lg:flex-row flex-col items-center ${images.length === 0 || loading ? 'justify-center' : 'justify-between'} w-full`}>
          {!loading && (
            <div className={`flex flex-col items-center ${images.length === 0 ? 'mx-auto' : ''}`}>
              <label className="flex flex-col items-center justify-center bg-[#FFF9D7] border-[#FFE96F] border-[2px] lg:w-[300px] lg:h-[300px] w-[200px] h-[200px] rounded-[360px] p-6 cursor-pointer">
                <FiUpload className="lg:text-[70px] text-[60px] mb-5" />
                <span className="lg:text-[20px] text-[18px] font-bold">사진업로드</span>
                <input 
                  type="file" 
                  multiple 
                  className="hidden" 
                  onChange={(event) => handleImageUpload(event, setImages)} 
                />
              </label>
            </div>
          )}
          {images.length > 0 && (
            <div className="flex flex-col justify-center items-center lg:mt-8 mt-0 relative w-full lg:w-auto">
              <div className={`grid grid-cols-2 lg:grid-cols-5 gap-4 overflow-y-auto lg:h-[442px] h-[330px] border-[#B2D170] border-[1px] mt-10 rounded-[10px] content-start p-3 ${loading ? 'bg-[#f4f4f4] opacity-50' : ''}`}>
                {images.map((file, index) => (
                  <div key={index} className="relative w-32 h-32 loading-container">
                    <img 
                      src={URL.createObjectURL(file)} 
                      alt={`upload-${index}`} 
                      className={`object-cover w-full h-full rounded-md ${loading ? 'loading' : ''}`} 
                    />
                    {!loading && (
                      <button 
                        onClick={() => handleDeleteImage(index, setImages)} 
                        className="absolute top-1 right-1 bg-red-600 text-white p-[2px] rounded-full"
                      >
                        <FaMinusCircle size={18} />
                      </button>
                    )}
                  </div>
                ))}
              </div>
              {loading && (
                <div className="absolute inset-0 flex justify-center items-center font-bold text-[20px]">
                  {loadingMessage}
                </div>
              )}
              <button 
                onClick={() => handleClassify(images, setLoading, navigate)} 
                className="animate-pulse-button flex items-center justify-center mt-5 font-bold py-2 px-4 bg-gradient-to-br from-[#FFF3B1] to-[#D5E4B4] rounded-[10px] w-[240px] h-[50px] transition-transform transform hover:scale-105 active:scale-95"
                disabled={loading}
              >
                {loading ? <PulseLoader color="#fff" /> : '아이별로 분류하기'}
              </button>
            </div>
          )}
        </div>
      </div>
      <style>{`
        @keyframes pulse {
          0%, 100% {
            transform: scale(1);
          }
          50% {
            transform: scale(1.05);
          }
        }
        .animate-pulse-button {
          animation: pulse 2s infinite;
        }
      `}</style>
    </TeacherLayout>
  );
}
