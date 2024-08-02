import axiosInstance from "./token/axiosInstance";

interface TaggedPhoto{
  childId: number;
  photos: number[];
}

interface SendData{
  albumName: string;
  taggedPhotos: TaggedPhoto[];
}

// 사진 분류하기
export async function createClassifyImages(classifyImages: File[]) {
  console.log("보내는 데이터: ", classifyImages);
  try {
    const formData = new FormData();
    
    classifyImages.forEach((file, index) => {
      formData.append('classifyImages', file);
    });

    const response = await axiosInstance.post('album/classify', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    });

    if (response.data.status === 'success') {
      console.log("분류 결과: ", response.data.data); // 확인 후 삭제
      return response.data.data;
    } else {
      throw new Error('Failed to get albums');
    }
  } catch (error) {
    console.error(error);
    throw error;
  }
}

// 특정 아이의 모든 앨범 조회
export async function getKidAllAlbums(childId: number) {
  try {
    const response = await axiosInstance.get(`album/child/${childId}`)

    if (response.data.status === 'success') {
      console.log(response.data.data) // 확인 후 삭제
      return response.data.data
    } else {
      throw new Error('Failed to get albums')
    }
  } catch (error) {
    console.error(error)
    throw error
  }
}

// 특정 아이의 특정 앨범 조회
export async function getKidAlbum(albumId: number) {
  try {
    const response = await axiosInstance.get(`album/${albumId}`)

    if (response.data.status === 'success') {
      console.log(response.data.data) // 확인 후 삭제
      return response.data.data
    } else {
      throw new Error('Failed to get album')
    }
  } catch (error) {
    console.error(error)
    throw error
  }
}

// 학부모에게 사진 전송
export async function sendAlbumToParent(sendData: SendData){
  try {
    const response = await axiosInstance.post('album', sendData);

    if (response.data.status === 'success') {
      return 'success'
    } else {
      throw new Error('Failed to get album')
    }
  } catch (error) {
    console.error(error)
    throw error
  }
}
