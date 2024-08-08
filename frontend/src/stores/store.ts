import { create } from 'zustand'
import { persist } from 'zustand/middleware'

export interface AppState {
  // 공통 데이터 타입
  username: string;
  password: string;
  passwordConfirm: string;
  name: string;
  nickname: string;
  email: string;
  tel: string;
  profile: File | undefined;
  setUsername: (username: string) => void;
  setPassword: (password: string) => void;
  setPasswordConfirm: (passwordConfirm: string) => void;
  setEmail: (email: string) => void;
  setName: (name: string) => void;
  setNickname: (nickname: string) => void;
  setTel: (tel: string) => void;
  setProfile: (profile: File | undefined) => void;

  // 자녀 데이터 타입
  gender: string;
  childName: string;
  birth: string;
  kindergartenId: number;
  kindergartenClassId: number;
  childProfile: File | undefined;
  setGender: (gender: string) => void;
  setChildName: (childName: string) => void;
  setBirth: (birth: string) => void;
  setKindergartenId: (kindergartenId: number) => void;
  setKindergartenClassId: (kindergartenClassId: number) => void;
  setChildProfile: (childProfile: File | undefined) => void;

  // 사용자 분리(학부모/선생님)
  userType: string;
  setUserType: (userType: string) => void;

  // 소셜 로그인을 위해 회원가입하는 사용자 구분
  isSocialLogin: boolean;
  setIsSocialLogin: (isSocialLogin: boolean) => void;

  // 상태 초기화 메서드
  resetState: () => void;
}

const useAppStore = create<AppState>()(
  persist(
    (set) => ({
      // 공통 데이터 상태 및 메서드
      username: '',
      password: '',
      passwordConfirm: '',
      name: '',
      nickname: '',
      email: '',
      tel: '',
      profile: undefined,
      setUsername: (username) => set(() => ({ username })),
      setPassword: (password) => set(() => ({ password })),
      setPasswordConfirm: (passwordConfirm) => set(() => ({ passwordConfirm })),
      setEmail: (email) => set(() => ({ email })),
      setName: (name) => set(() => ({ name })),
      setNickname: (nickname) => set(() => ({ nickname })),
      setTel: (tel) => set(() => ({ tel })),
      setProfile: (profile) => set(() => ({ profile })),

      // 자녀 데이터 상태 및 메서드
      gender: '',
      childName: '',
      birth: '',
      kindergartenId: 0,
      kindergartenClassId: 0,
      childProfile: undefined,
      setGender: (gender) => set(() => ({ gender })),
      setChildName: (childName) => set(() => ({ childName })),
      setBirth: (birth) => set(() => ({ birth })),
      setKindergartenId: (kindergartenId) => set(() => ({ kindergartenId })),
      setKindergartenClassId: (kindergartenClassId) => set(() => ({ kindergartenClassId })),
      setChildProfile: (childProfile) => set(() => ({ childProfile })),

      // 사용자 분리(학부모/선생님) 상태 및 메서드
      userType: '',
      setUserType: (userType) => set(() => ({ userType })),

      // 소셜 로그인을 위해 회원가입하는 사용자 상태 및 메서드
      isSocialLogin: false,
      setIsSocialLogin: (isSocialLogin) => set({ isSocialLogin }),

      // 상태 초기화 메서드
      resetState: () =>
        set(() => ({
          username: '',
          password: '',
          passwordConfirm: '',
          name: '',
          nickname: '',
          email: '',
          tel: '',
          profile: undefined,
          gender: '',
          childName: '',
          birth: '',
          kindergartenId: 0,
          kindergartenClassId: 0,
          childProfile: undefined,
          userType: '',
          isSocialLogin: false,
        })),
    }),
    {
      name: 'user-storage',
      partialize: (state) => ({ userType: state.userType }), // userType만 저장
      storage: {
        getItem: (name) => {
          const item = sessionStorage.getItem(name)
          return item ? JSON.parse(item) : null
        },
        setItem: (name, value) => {
          sessionStorage.setItem(name, JSON.stringify(value))
        },
        removeItem: (name) => {
          sessionStorage.removeItem(name)
        },
      },
    }
  )
)

export default useAppStore
