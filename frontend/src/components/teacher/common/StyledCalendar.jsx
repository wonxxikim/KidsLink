import styled from 'styled-components';
import Calendar from "react-calendar";

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

export default StyledCalendar;
