import { useState, useEffect } from 'react';
import Title from "../../components/teacher/common/Title";
import NoticeItem from "../../components/teacher/notice/NoticeItem";
import { MdOutlineNavigateBefore, MdOutlineNavigateNext } from "react-icons/md";
import useModal from "../../hooks/teacher/useModal";
import { getAllNotices, createNotice } from '../../api/notice';
import { showToastError } from '../../components/teacher/common/ToastNotification';
import ToastNotification from '../../components/teacher/common/ToastNotification';
import TeacherLayout from "../../layouts/TeacherLayout";
import { LuPencilLine } from "react-icons/lu";
import daramgi from "../../assets/teacher/notice-daramgi.png"

export default function TeacherNotice() {
    const { openModal, closeModal, Modal } = useModal();
    const [currentPage, setCurrentPage] = useState(1);
    const [searchType, setSearchType] = useState("title");
    const [searchTitle, setSearchTitle] = useState("");
    const [searchDate, setSearchDate] = useState("");
    const [notices, setNotices] = useState([]);

    const ITEMS_PER_PAGE = 4;

    useEffect(() => {
        const fetchNotices = async () => {
            try {
                const fetchedNotices = await getAllNotices();
                const sortedNotices = fetchedNotices.sort((a, b) => b.noticeBoardId - a.noticeBoardId);
                setNotices(sortedNotices);
            } catch (error) {
                console.error('Failed to fetch notices:', error);
            }
        };

        fetchNotices();
    }, []);

    const filteredItems = notices.filter(item => {
        if (searchType === "title") {
            return item.title.includes(searchTitle);
        } else if (searchType === "date") {
            return searchDate === "" || item.noticeBoardDate === searchDate;
        }
        return true;
    });

    const totalPages = Math.ceil(filteredItems.length / ITEMS_PER_PAGE);
    const startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
    const displayedItems = filteredItems.slice(startIndex, startIndex + ITEMS_PER_PAGE);

    const handlePreviousPage = () => {
        if (currentPage > 1) {
            setCurrentPage(currentPage - 1);
        }
    };

    const handleNextPage = () => {
        if (currentPage < totalPages) {
            setCurrentPage(currentPage + 1);
        }
    };

    const handleSearchTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTitle(e.target.value);
        setCurrentPage(1);
    };

    const handleSearchDateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchDate(e.target.value);
        setCurrentPage(1);
    };

    const handleSearchTypeChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
        setSearchType(e.target.value);
        setSearchTitle("");
        setSearchDate("");
        setCurrentPage(1);
    };

    const renderModalContent = () => (
        <CreateNoticeForm closeModal={closeModal} setNotices={setNotices} />
    );

    const openCreateModal = () => {
        openModal(renderModalContent());
    };

    return (
        <TeacherLayout
            activeMenu="notice"
            setActiveMenu={() => {}}
            titleComponent={<Title title="알림장" />}
            imageSrc={daramgi} 
        >
            <div className="relative w-full mt-8 mb-32 px-[15px] flex flex-col items-center">
                <button onClick={openCreateModal} className="absolute lg:right-[35px] right-[80px] border-[2px] border-[#7C7C7C] bg-[#E3EEFF] px-3 py-1 font-bold rounded-[10px] hover:bg-[#D4DDEA] flex flex-row items-center">
                    <LuPencilLine className="mr-2" />알림장 작성하기
                </button>
                <div className="flex justify-center w-full lg:mt-[5px] mt-[50px]">
                    <div className="lg:w-1/2 w-full p-0 flex mb-3">
                        <select
                            value={searchType}
                            onChange={handleSearchTypeChange}
                            className="w:1/3 border-2 border-[#f4f4f4] focus:outline-none text-lg rounded-[8px] px-2 py-1"
                        >
                            <option value="title">제목 검색</option>
                            <option value="date">날짜 검색</option>
                        </select>
                        {searchType === "title" ? (
                            <input
                                type="text"
                                placeholder="제목 검색"
                                value={searchTitle}
                                onChange={handleSearchTitleChange}
                                className="w-3/4 w:2/3 border-2 border-[#f4f4f4] focus:outline-none text-lg ml-2 custom-placeholder::placeholder px-2 py-1 rounded-[8px]"
                            />
                        ) : (
                            <input
                                type="date"
                                value={searchDate}
                                onChange={handleSearchDateChange}
                                className="w-3/4 w:2/3 border-2 border-[#f4f4f4] focus:outline-none text-lg ml-2 custom-placeholder::placeholder px-2 py-1 rounded-[8px]"
                                max="9999-12-31"
                            />
                        )}
                    </div>
                </div>
                <div className="w-full lg:w-auto">
                    {displayedItems.map((item, index) => (
                        <NoticeItem
                            key={index}
                            id={item.noticeBoardId}
                            title={item.title}
                            date={item.noticeBoardDate}
                            content={item.content}
                        />
                    ))}
                </div>
                <div className="flex justify-center w-full">
                    <nav>
                        <ul className="inline-flex items-center">
                            <li>
                                <button
                                    onClick={handlePreviousPage}
                                    className={`py-2 px-5 ${currentPage === 1 ? 'text-gray-400' : 'text-[#B2D170]'}`}
                                    disabled={currentPage === 1}
                                >
                                    <MdOutlineNavigateBefore className="text-[23px]" />
                                </button>
                            </li>
                            {[...Array(totalPages).keys()].map(page => (
                                <li key={page + 1}>
                                    <button
                                        onClick={() => setCurrentPage(page + 1)}
                                        className={`px-4 py-2 mx-1 font-bold rounded-[360px] ${currentPage === page + 1 ? 'bg-[#B2D170] text-[#fff]' : 'text-[#8CAD1E]'}`}
                                    >
                                        {page + 1}
                                    </button>
                                </li>
                            ))}
                            <li>
                                <button
                                    onClick={handleNextPage}
                                    className={`py-2 px-5 ${currentPage === totalPages ? 'text-gray-400' : 'text-[#B2D170]'}`}
                                    disabled={currentPage === totalPages}
                                >
                                    <MdOutlineNavigateNext className="text-[23px]" />
                                </button>
                            </li>
                        </ul>
                    </nav>
                </div>
            </div>
            <Modal />
            <ToastNotification />
        </TeacherLayout>
    );
}

function CreateNoticeForm({ closeModal, setNotices }) {
    const [newNoticeTitle, setNewNoticeTitle] = useState("");
    const [newNoticeContent, setNewNoticeContent] = useState("");

    const handleCreateNotice = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!newNoticeTitle.trim()) {
            showToastError(<div>제목을 작성해주세요</div>);
            return;
        }

        if (!newNoticeContent.trim()) {
            showToastError(<div>내용을 작성해주세요</div>);
            return;
        }

        const noticeData = {
            title: newNoticeTitle,
            content: newNoticeContent,
            noticeBoardDate: new Date().toISOString().split('T')[0]
        };

        try {
            await createNotice(noticeData);
            const fetchedNotices = await getAllNotices();
            const sortedNotices = fetchedNotices.sort((a, b) => b.noticeBoardId - a.noticeBoardId);
            setNotices(sortedNotices);
            setNewNoticeTitle("");
            setNewNoticeContent("");
            closeModal();
        } catch (error) {
            console.error('Failed to create notice:', error);
        }
    };

    return (
        <div className="w-[90vw] lg:w-[500px]">
            <form onSubmit={handleCreateNotice}>
                <div className="mb-4 flex flex-col lg:flex-row">
                    <label className="block mr-3 mb-1 font-bold whitespace-nowrap text-[18px]">제목</label>
                    <input
                        className="border border-gray-300 p-2 rounded w-full"
                        value={newNoticeTitle}
                        onChange={(e) => setNewNoticeTitle(e.target.value)}
                    />
                </div>
                
                <div className="mb-4 flex flex-col lg:flex-row">
                    <label className="block mr-3 mb-1 font-bold whitespace-nowrap text-[18px]">내용</label>
                    <textarea
                        className="border border-gray-300 p-2 rounded w-full"
                        rows={10}
                        value={newNoticeContent}
                        onChange={(e) => setNewNoticeContent(e.target.value)}
                    ></textarea>
                </div>
                <div className="flex items-center justify-center">
                    <button
                        type="submit"
                        className="w-[70px] h-[38px] border-[2px] border-[#7C7C7C] bg-[#E3EEFF] px-3 py-1 font-bold rounded-[8px] hover:bg-[#D4DDEA]"
                    >
                        등록
                    </button>
                </div>
            </form>
        </div>
    );
}
