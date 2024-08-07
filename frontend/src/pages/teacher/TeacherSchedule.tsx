import React, { useState, useCallback, useRef, useEffect } from "react";
import { DndProvider, useDrag, useDrop } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import Calendar from "react-calendar";
import NavigateBack from "../../components/teacher/common/NavigateBack";
import TeacherHeader from "../../components/teacher/common/TeacherHeader";
import Title from "../../components/teacher/common/Title";
import moment from "moment";
import { FaRegCalendar, FaXmark } from "react-icons/fa6";
import styled from 'styled-components';
import { createTeacherSchedule, createTeacherScheduleCheck, deleteTeacherSchedule, getTeacherSchedules } from "../../api/schedule";

const StyledCalendar = styled(Calendar)`
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  font-family: 'font-KoPubDotum';
  width: 450px;
  padding: 30px;
  max-width: 100%;
  background-color: #fff;
  color: #222;
  border-width: 2px;
  border-radius: 10px;
  line-height: 1.125em;

    .react-calendar { 
        display: flex;
        flex-direction: column;
        justify-content: center;
        align-items: center;
        font-family: 'font-KoPubDotum';
        width: 450px;
        padding: 30px;
        max-width: 100%;
        background-color: #fff;
        color: #222;
        border-width: 2px;
        border-radius: 10px;
        line-height: 1.125em;
    }

    .react-calendar__navigation__next-button--years {
        display: none;
    }

    /* 년-월 */
    .react-calendar__navigation{
        margin-bottom: 25px;
        
    }

    .react-calendar__navigation__label > span {
        color: #000;
        font-family: SUIT Variable;
        font-size: 20px;
        font-weight: bold;
        line-height: 140%;
        margin-left: 30px;
        margin-right: 30px;
    }

    .react-calendar__navigation__prev-button{
        font-size: 25px;
    }

    .react-calendar__navigation__next-button{
        font-size: 25px;
    }


    /* 요일 */
    .react-calendar__month-view__weekdays__weekday{
        border-bottom: none;
        font-family: 'font-KoPubDotum';
        padding: 8px !important;
        color: #000;
        font-size: 16px;
        font-weight: bold;
        margin-bottom: 13px;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .react-calendar__month-view__days__day {
        color: #fff;
        font-size: 18px;
        font-weight: bold;
        width: 44px;
        height: 44px;
        text-align: center;
        margin: 0;
        padding: 0;
    }

    .react-calendar__month-view__weekdays {
        display: flex;
        justify-content: center;
    }

    /* 요일 밑줄 제거 */
    .react-calendar__month-view__weekdays abbr {
        text-decoration: none;
        font-weight: 800;
    }

    
    /* 이번 달 일자 */
    .react-calendar__tile{
        color: #000;
        font-size: 18px;
        font-weight: bold;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        align-items: center;
    }

    .react-calendar__tile--weekend{
        color: #000;
        font-size: 18px;
        font-weight: bold;
        display: flex;
        flex-direction: column;
        justify-content: flex-start;
        align-items: center;
    }

    /* 저번 달 & 다음 달 일자 */
    .react-calendar__month-view__days__day--neighboringMonth{
        font-family: 'font-KoPubDotum';
        color: #5F5F5F;
        font-size: 18px;
        font-weight: bold;
        width: 44px;
        height: 44px;
    }

    /* 날짜 사이 간격 */
    .react-calendar__tile {
        font-family: 'font-KoPubDotum';
        padding: 10px;
        margin-bottom: 12px;
        font-size: 17px;
        display: flex;
        justify-content: center;
    }


    .react-calendar__tile:hover {
        border-radius: 20%;
    }

    /* 오늘 날짜 */
    .react-calendar__tile--now {
    background-color: #f6f6f6;
    color: #363636;
    border-radius: 20%;
    }

    .react-calendar__tile--now:enabled:hover,
    .react-calendar__tile--now:enabled:focus {
    background-color: #f6f6f6;
    border-radius: 20%;
    }

    /* 선택된 날짜의 배경색 변경 */
    .react-calendar__tile--active {
    border: 2px solid #8CAD1E;
    background-color: #D2E591;
    color: #000000;
    border-radius: 20%;
    }

    .react-calendar__tile--active:enabled:hover,
    .react-calendar__tile--active:enabled:focus {
    border: 2px solid #8CAD1E;
    background-color: #D2E591;
    color: #000000;
    border-radius: 20%;

    } 
`;

interface ScheduleItemType {
    id: number;
    content: string;
    confirmationStatus: string;
}

const ItemType = {
    SCHEDULE_ITEM: 'scheduleItem',
};

interface ScheduleItemProps {
    id: number;
    content: string;
    confirmationStatus: string;
    index: number;
    moveItem: (fromIndex: number, toIndex: number) => void;
    deleteItem: (id: number) => void;
    toggleComplete: (id: number) => void;
}

const ScheduleItem: React.FC<ScheduleItemProps> = ({ id, content, confirmationStatus, index, moveItem, deleteItem, toggleComplete }) => {
    const ref = useRef<HTMLDivElement>(null);
    const [, drop] = useDrop({
        accept: ItemType.SCHEDULE_ITEM,
        hover: (draggedItem: { index: number }) => {
            if (draggedItem.index !== index) {
                moveItem(draggedItem.index, index);
                draggedItem.index = index;
            }
        },
    });

    const [{ isDragging }, drag] = useDrag({
        type: ItemType.SCHEDULE_ITEM,
        item: { id, index },
        collect: (monitor) => ({
            isDragging: monitor.isDragging(),
        }),
    });

    // drag(drop(ref));  // 주석 해제하면 드래그앤드롭 할수있음

    return (
        <div
            ref={ref}
            className={`flex flex-row m-2 items-center ${isDragging ? 'opacity-50' : ''}`}
        >
            <input
                type="checkbox"
                className="mr-2 w-[19px] h-[19px] accent-[#757575]"
                checked={confirmationStatus === "T"}
                disabled={confirmationStatus === "T"}
                onChange={() => toggleComplete(id)}
            />
            <p className={`${confirmationStatus === "T" ? 'text-[#B8B8B8]' : ''} text-[18px] flex-grow`}>{content}</p>
            <button onClick={() => deleteItem(id)} className="text-red-500 hover:text-red-700">
                <FaXmark />
            </button>
        </div>
    );
};

type ValuePiece = Date | null;

export default function TeacherSchedule() {
    const [date, setDate] = useState<ValuePiece | [ValuePiece, ValuePiece]>(new Date());
    const [scheduleItems, setScheduleItems] = useState<ScheduleItemType[]>([]);
    const [time, setTime] = useState('');
    const [todo, setTodo] = useState('');

    const fetchSchedules = async () => {
        try{
            const fetchedSchedules = (await getTeacherSchedules(formatDate(date))).teacherSchedules;
            fetchedSchedules.sort((a, b) => {
                // 우선순위: completed === "T"
                if (a.confirmationStatus === "T" && b.confirmationStatus !== "T") return -1;
                if (a.confirmationStatus !== "T" && b.confirmationStatus === "T") return 1;

                // 2차 정렬: content 값
                const isANumeric = /^\d/.test(a.content);
                const isBNumeric = /^\d/.test(b.content);

                if (isANumeric && !isBNumeric) return -1;
                if (!isANumeric && isBNumeric) return 1;
                if (a.content < b.content) return -1;
                if (a.content > b.content) return 1;

                // 모든 조건이 같을 경우
                return 0;
            });
            setScheduleItems(fetchedSchedules);
        } catch (error) {
            console.error("Failed to fetch schedules:", error);
        }
    }


    useEffect(() => {
        fetchSchedules();
    }, [date])

    const formatDate = (date: ValuePiece | [ValuePiece, ValuePiece]): string => {
        if (date instanceof Date) {
            return moment(date).format("YYYY-MM-DD");
        } else if (Array.isArray(date) && date[0] instanceof Date) {
            return moment(date[0]).format("YYYY-MM-DD");
        }
        return '';
    };

    const deleteItem = async (id: number) => {
        await deleteTeacherSchedule(id);
        fetchSchedules();
    };

    const moveItem = useCallback((fromIndex: number, toIndex: number) => {
        const updatedItems = [...scheduleItems];
        const [movedItem] = updatedItems.splice(fromIndex, 1);
        updatedItems.splice(toIndex, 0, movedItem);
        setScheduleItems(updatedItems);
    }, [scheduleItems]);

    const handleAddScheduleItem = async () => {
        const scheduleData = {
            date: formatDate(date),
            content: time + ' ' + todo,
        };

        try {
            await createTeacherSchedule(scheduleData);
            fetchSchedules();
            setTime('');
            setTodo('');
        } catch (error) {
            console.error('Failed to create notice:', error);
        }
    };

    const toggleComplete = async (id: number) => {
        await createTeacherScheduleCheck(id);
        fetchSchedules();
    };

    return (
        <DndProvider backend={HTML5Backend}>
            <TeacherHeader />
            <div className="flex flex-col mt-[90px]">
                <NavigateBack backPage="홈" backLink='/' />
                <Title title="일정" />
                <div className="px-[150px] mt-5 flex flex-row justify-between items-center mt-10">
                    <StyledCalendar
                        onChange={setDate}
                        value={date}
                        formatDay={(locale: string, date: Date) => date.toLocaleString("en", { day: "numeric" })}
                        next2Label={null}
                        prev2Label={null}
                    />
                    <div className="w-[637px]">
                        <div className="flex flex-row justify-between mb-2">
                            <div className="text-[22px] flex flex-row items-center h-[22px] font-bold text-[#8CAD1E] my-2">
                                <FaRegCalendar className="mr-3" />
                                {formatDate(date)}
                            </div>
                        </div>
                        <div className="border-[2px] border-[#8CAD1E] rounded-[10px] h-[330px]">
                            <div className="overflow-y-auto custom-scrollbar h-[310px] m-[10px]">
                            {scheduleItems.length === 0 ? (
                                <div className="flex justify-center items-center h-[310px]">
                                일정이 없어요.
                                </div>
                                ) : (
                                scheduleItems.map(({ id, content, confirmationStatus }, index) => (
                                <ScheduleItem
                                    key={id}
                                    id={id}
                                    content={content}
                                    confirmationStatus={confirmationStatus}
                                    index={index}
                                    moveItem={moveItem}
                                    deleteItem={deleteItem}
                                    toggleComplete={toggleComplete}
                                />
                                ))
                            )}
                            </div>
                        </div>
                        <div className="flex flex-row justify-between items-center mt-3">
                            <input
                                className="border w-[150px] h-[40px] border-[2px] border-[#B8B8B8] mr-1 rounded-[10px] p-1"
                                type="time"
                                value={time}
                                onChange={(e) => setTime(e.target.value)}
                            />
                            <input
                                className="border w-[580px] h-[40px] bg-[#F8F8F8] border-[2px] rounded-[10px] border-[#B8B8B8] mr-1 p-1"
                                type="text"
                                value={todo}
                                onChange={(e) => setTodo(e.target.value)}
                            />
                            <button
                                className="font-bold border-[2px] border-[#B8B8B8] text-[#B8B8B8] w-[65px] h-[40px] rounded-[10px] hover:bg-[#F3F3F3]"
                                onClick={handleAddScheduleItem}
                            >
                                추가
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </DndProvider>
    );
};
