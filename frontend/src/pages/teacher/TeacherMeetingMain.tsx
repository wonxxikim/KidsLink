// TeacherMeetingMain.tsx
import MeetingMainButton from "../../components/parent/meeting/MeetingMainButton";
import Title from "../../components/teacher/common/Title";
import CalendarImg from "../../assets/teacher/calendar_img.png";
import CheckImg from "../../assets/teacher/check_img.png";
import VideoConsultingImg from "../../assets/teacher/video_consulting_img.png";
import RecordImg from "../../assets/teacher/record_img.png";
import TeacherLayout from "../../layouts/TeacherLayout";
import daramgi from "../../assets/teacher/meeting-daramgi.png"

export default function TeacherMeetingMain() {
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
            titleComponent={<Title title="화상상담" tabs={tabs} />}
            imageSrc={daramgi} 
        >
            <div className="relative w-full mt-10 mb-32 px-4 lg:px-36 flex flex-col items-center">

                <div className="w-full flex justify-center items-center">
                    <div className="flex flex-row flex-wrap w-full lg:w-[1135px] items-center justify-between mt-10 lg:mt-[50px] space-y-8 lg:space-y-0">
                        <MeetingMainButton 
                            title="상담가능시간 open" 
                            description="학부모 상담이 가능한 시간을 선택해주세요. 해당 시간에 학부모가 상담 신청을 할 수 있어요." 
                            link="/meeting/reservation" 
                            image={CalendarImg} 
                            imgWidth={1000} 
                            imgTopLg={65}
                            imgTopSm={-26}
                            imgLeftLg={58}
                            imgLeftSm={85}
                        />
                        <MeetingMainButton 
                            title="상담시간 확정" 
                            description="키즈링크만의 알고리즘으로 최대한 많은 학부모와 상담을 할 수 있도록 상담 시간을 확정해드려요." 
                            link="/meeting/confirm" 
                            image={CheckImg} 
                            imgWidth={182} 
                            imgTopLg={90}
                            imgTopSm={36}
                            imgLeftLg={85}
                            imgLeftSm={160}
                        />
                        <MeetingMainButton 
                            title="예약된 화상상담" 
                            description="예약된 화상상담 일정을 확인하세요. 10분 전부터 입장할 수 있어요 :)" 
                            link="/meeting/scheduled" 
                            image={VideoConsultingImg} 
                            imgWidth={680} 
                            imgTopLg={88}
                            imgTopSm={0}
                            imgLeftLg={37}
                            imgLeftSm={60}
                        />
                        <MeetingMainButton 
                            title="녹화된 상담" 
                            description="예약된 화상상담 일정을 확인하세요. 10분 전부터 입장할 수 있어요 :)" 
                            link="/meeting/recordings" 
                            image={RecordImg} 
                            imgWidth={130} 
                            imgTopLg={115}
                            imgTopSm={0}
                            imgLeftLg={115}
                            imgLeftSm={60}
                        />
                    </div>
                </div>
            </div>
        </TeacherLayout>
    );
}
