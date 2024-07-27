import axiosInstance from './token/axiosInstance'
import { useNoticeStore } from '../stores/noticeStore'

interface Notice {
  noticeBoardId: number;
  teacherName: string;
  title: string;
  content: string;
  noticeBaordDate: string; // 데이터 오타로 옴
}

interface NoticeDetail {
  noticeBoardId: number;
  teacherName: string;
  title: string;
  content: string;
  noticeBaordDate: string; 
}

interface NoticeData {
  title: string;
  content: string;
}

// 전체 알림장 조회
export async function getAllNotices(): Promise<Notice[]> {
  try {
    const response = await axiosInstance.get('noticeboard')

    if (response.data.status === 'success') {
      console.log(response.data.data) // 확인 후 삭제

      const notices: Notice[] = response.data.data;
      const setNotices = useNoticeStore.getState().setNotices;
      setNotices(notices) // zustand 스토어 업데이트

      return notices
    } else {
      throw new Error('Failed to fetch notices')
    }
  } catch (error) {
    console.error(error)
    throw error
  }
}

// 알림장 상세 조회
export async function getNoticeDetail(noticeBoardId: number): Promise<NoticeDetail> {
  try {
    const response = await axiosInstance.get(`/noticeboard/${noticeBoardId}`)

    if (response.data.status === 'success') {
      console.log(response.data.data) // 확인 후 삭제
      return response.data.data
    } else {
      throw new Error('Failed to fetch notice-detail')
    }
  } catch (error) {
    console.error(error)
    throw error
  }
}

// 알림장 작성
export async function createNotice(notice: NoticeData) {
  try {
    const response = await axiosInstance.post('noticeboard', notice)

    if (response.data.status === "success") {
      console.log("create-notice success")
      return response.data.data;
    } else {
      throw new Error('create-notice failed: ' + response.data.message)
    }
  } catch (error) {
    console.error(error)
    throw error
  }
}