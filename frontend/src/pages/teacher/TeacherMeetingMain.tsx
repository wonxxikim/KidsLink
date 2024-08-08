import MeetingMainButton from "../../components/parent/meeting/MeetingMainButton";
import NavigateBack from "../../components/teacher/common/NavigateBack";
import TeacherHeader from "../../components/teacher/common/TeacherHeader";
import Title from "../../components/teacher/common/Title";
import CalendarImg from "../../assets/teacher/calendar_img.png";
import CheckImg from "../../assets/teacher/check_img.png";
import VideoConsultingImg from "../../assets/teacher/video_consulting_img.png";
import { Link } from "react-router-dom";

export default function TeacherMeetingMain() {
    return (
        <>
            <TeacherHeader />
            <div className="mt-[130px] px-[20px] lg:px-[130px]">
                <NavigateBack backPage="홈" backLink='/' />
                <Title title="화상상담" />
                {/* 이동하기위한 요소 */}
                <Link to="/meeting/recordings">Go to Recordings</Link>

                <div className="flex flex-col lg:flex-row items-center justify-center lg:mt-[95px] mt-10 space-y-8 lg:space-y-0 lg:space-x-10">
                    <MeetingMainButton 
                        title="상담가능시간 open" 
                        description="학부모 상담이 가능한 시간을 선택해주세요. 해당 시간에 학부모가 상담 신청을 할 수 있어요." 
                        link="/meeting/reservation" 
                        image={CalendarImg} 
                        imgWidth={550} 
                        imgTopLg={62}
                        imgTopSm={-26}
                        imgLeftLg={82}
                        imgLeftSm={85}
                    />
                    <MeetingMainButton 
                        title="상담시간 확정" 
                        description="키즈링크만의 알고리즘으로 최대한 많은 학부모와 상담을 할 수 있도록 상담 시간을 확정해드려요." 
                        link="/meeting/confirm" 
                        image={CheckImg} 
                        imgWidth={218} 
                        imgTopLg={140}
                        imgTopSm={36}
                        imgLeftLg={170}
                        imgLeftSm={160}
                    />
                    <MeetingMainButton 
                        title="예약된 화상상담" 
                        description="예약된 화상상담 일정을 확인하세요. 10분 전부터 입장할 수 있어요 :)" 
                        link="/meeting/scheduled" 
                        image={VideoConsultingImg} 
                        imgWidth={450} 
                        imgTopLg={97}
                        imgTopSm={0}
                        imgLeftLg={57}
                        imgLeftSm={60}
                    />
                </div>
            </div>
        </>
    );
}
