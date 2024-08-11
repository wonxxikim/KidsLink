import React, { useEffect, useState } from "react";
import { fetchRecordingsList } from "../../utils/openvidu";
import { Recording } from "../../types/openvidu";
import { handleDownload } from "../../api/openvidu";
import Title from "../../components/teacher/common/Title";
import TeacherLayout from '../../layouts/TeacherLayout';
import daramgi from "../../assets/teacher/meeting-daramgi.png"

// 페이지 디자인용으로 진행하고, 나중에는 삭제해야합니다!
const dummyRecordings: Recording[] = [
  { id: "1", name: "Recording 1", url: "/path/to/recording1" },
  { id: "2", name: "Recording 2", url: "/path/to/recording2" },
  { id: "3", name: "Recording 3", url: "/path/to/recording3" },
];

const TeacherMeetingRecordingList: React.FC = () => {
  const [recordings, setRecordings] = useState<Recording[]>([]);
  const [noRecordings, setNoRecordings] = useState(false); // 녹화본이 없는 상태 관리

  useEffect(() => {
    async function fetchAllRecordings() {
      try {
        await fetchRecordingsList(setRecordings);
      } catch (error) {
        console.error("Failed to fetch recordings:", error);
      }
    }

    fetchAllRecordings();
  }, []);

  useEffect(() => {
    if (!recordings || recordings.length === 0) {
      setRecordings(dummyRecordings);
      // setNoRecordings(true); // 녹화본이 없을 때 상태를 true로 설정
    } else {
      setNoRecordings(false); // 녹화본이 있을 때 상태를 false로 설정
    }
    console.log(recordings);
  }, [recordings]);

  const handleDelete = (id: string) => {
    setRecordings(recordings.filter((recording) => recording.id !== id));
  };

  const tabs = [
    { label: "상담가능시간 open", link: "/meeting/reservation" },
    { label: "상담시간 확정", link: "/meeting/confirm" },
    { label: "예약된 화상상담", link: "/meeting/scheduled" },
    { label: "녹화된 상담", link: "/meeting/recordings" },
];

return (
    <TeacherLayout
        activeMenu="meeting"
        setActiveMenu={() => {}}
        titleComponent={<Title title="녹화된 상담" tabs={tabs} />}
        imageSrc={daramgi} 
    >
      <div className="w-full mt-10 mb-32 px-4 lg:px-8 py-6 lg:py-8">
        {noRecordings ? (
          <p className="text-gray-600">저장된 녹화본이 없습니다.</p>
        ) : (
          <ul className="list-none p-0">
            {recordings.map((recording) => (
              <li
                key={recording.id}
                className="flex flex-col lg:flex-row justify-between items-center mb-4 p-4 border border-gray-200 rounded-md shadow-sm"
              >
                <span className="text-lg font-semibold">{recording.name}</span>
                <div className="flex flex-col lg:flex-row mt-2 lg:mt-0">
                  <button
                    onClick={() => handleDownload(recording.url)}
                    className="mb-2 lg:mb-0 lg:mr-2 px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-700 transition duration-200"
                  >
                    다운로드
                  </button>
                  <button
                    onClick={() => handleDelete(recording.id)}
                    className="px-4 py-2 bg-red-500 text-white rounded hover:bg-red-700 transition duration-200"
                  >
                    삭제
                  </button>
                </div>
              </li>
            ))}
          </ul>
        )}
      </div>
    </TeacherLayout>
  );
};

export default TeacherMeetingRecordingList;
