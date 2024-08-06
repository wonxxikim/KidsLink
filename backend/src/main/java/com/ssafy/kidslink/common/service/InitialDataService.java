package com.ssafy.kidslink.common.service;

import com.ssafy.kidslink.application.album.domain.Album;
import com.ssafy.kidslink.application.album.repository.AlbumRepository;
import com.ssafy.kidslink.application.bus.domain.Bus;
import com.ssafy.kidslink.application.bus.domain.BusStop;
import com.ssafy.kidslink.application.bus.domain.BusStopChild;
import com.ssafy.kidslink.application.bus.repository.BusRepository;
import com.ssafy.kidslink.application.bus.repository.BusStopRepository;
import com.ssafy.kidslink.application.bus.repository.BusStopChildRepository;
import com.ssafy.kidslink.application.child.domain.Child;
import com.ssafy.kidslink.application.child.repository.ChildRepository;
import com.ssafy.kidslink.application.diary.domain.Diary;
import com.ssafy.kidslink.application.diary.repository.DiaryRepository;
import com.ssafy.kidslink.application.diary.repository.ImageDiaryRepository;
import com.ssafy.kidslink.application.diary.service.DiaryService;
import com.ssafy.kidslink.application.document.domain.Absent;
import com.ssafy.kidslink.application.document.domain.Dosage;
import com.ssafy.kidslink.application.document.repository.AbsentRepository;
import com.ssafy.kidslink.application.document.repository.DosageRepository;
import com.ssafy.kidslink.application.image.domain.Image;
import com.ssafy.kidslink.application.image.dto.ImageDTO;
import com.ssafy.kidslink.application.image.mapper.ImageMapper;
import com.ssafy.kidslink.application.image.repository.ImageRepository;
import com.ssafy.kidslink.application.image.service.ImageService;
import com.ssafy.kidslink.application.imagealbum.repository.ImageAlbumRepository;
import com.ssafy.kidslink.application.kindergarten.domain.Kindergarten;
import com.ssafy.kidslink.application.kindergarten.domain.KindergartenClass;
import com.ssafy.kidslink.application.kindergarten.repository.KindergartenClassRepository;
import com.ssafy.kidslink.application.kindergarten.repository.KindergartenRepository;
import com.ssafy.kidslink.application.meeting.repository.MeetingScheduleRepository;
import com.ssafy.kidslink.application.meeting.repository.MeetingTimeRepository;
import com.ssafy.kidslink.application.meeting.repository.SelectedMeetingRepository;
import com.ssafy.kidslink.application.noticeboard.domain.NoticeBoard;
import com.ssafy.kidslink.application.noticeboard.repository.NoticeBoardRepository;
import com.ssafy.kidslink.application.notification.respository.ParentNotificationRepository;
import com.ssafy.kidslink.application.notification.respository.TeacherNotificationRepository;
import com.ssafy.kidslink.application.parent.domain.Parent;
import com.ssafy.kidslink.application.parent.repository.ParentRepository;
import com.ssafy.kidslink.application.schedule.domain.Schedule;
import com.ssafy.kidslink.application.schedule.repository.ScheduleRepository;
import com.ssafy.kidslink.application.teacher.domain.Teacher;
import com.ssafy.kidslink.application.teacher.repository.TeacherRepository;
import com.ssafy.kidslink.common.dto.CustomMultipartFile;
import com.ssafy.kidslink.common.enums.BoardingStatus;
import com.ssafy.kidslink.common.enums.ConfirmationStatus;
import com.ssafy.kidslink.common.enums.Gender;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InitialDataService {

    private final KindergartenRepository kindergartenRepository;
    private final KindergartenClassRepository kindergartenClassRepository;
    private final ParentRepository parentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TeacherRepository teacherRepository;
    private final ChildRepository childRepository;
    private final NoticeBoardRepository noticeBoardRepository;
    private final DiaryRepository diaryRepository;
    private final AbsentRepository absentRepository;
    private final DosageRepository dosageRepository;
    private final ImageService imageService;
    private final ScheduleRepository scheduleRepository;
    private final TeacherNotificationRepository teacherNotificationRepository;
    private final ParentNotificationRepository parentNotificationRepository;
    private final AlbumRepository albumRepository;
    private final MeetingTimeRepository meetingTimeRepository;
    private final MeetingScheduleRepository meetingScheduleRepository;
    private final ImageRepository imageRepository;
    private final ImageMapper imageMapper;
    private final SelectedMeetingRepository selectedMeetingRepository;
    private final BusStopRepository busStopRepository;
    private final BusStopChildRepository busStopChildRepository;
    private final ImageDiaryRepository imageDiaryRepository;
    private final ImageAlbumRepository imageAlbumRepository;
    private final BusRepository busRepository;
    private final DiaryService diaryService;

    @Value("${server.url}")
    private String serverUrl;
    @Value("${file.profile-dir}")
    private String profileDir;

    protected List<ImageDTO> initProfiles;
    protected List<ImageDTO> child2Profiles;
    protected List<ImageDTO> child3Profiles;

    @Transactional
    public String initializeData() {
        initProfiles = new ArrayList<>();
        child2Profiles = new ArrayList<>();
        child3Profiles = new ArrayList<>();

        deleteAll();

        // TODO #0 초기 이미지 삽입
        saveInitProfiles();

        // 이미지 데이터 삽입
        init();

        return "데이터 세팅 성공";
    }

    @Transactional
    protected void init() {
        // TODO #1 유치원 초기 데이터 삽입
        List<Kindergarten> kindergartens = getKindergartens();
        kindergartenRepository.saveAll(kindergartens);

        // TODO #2 유치원 반 초기 데이터 삽입
        List<KindergartenClass> kindergartenClasses = kindergartenClassRepository.saveAll(getKindergartenClasses(kindergartens));

        // TODO #3 부모님 데이터 삽입
        List<Parent> parents = parentRepository.saveAll(getParents());

        // TODO #4 선생님 데이터 삽입
        List<Teacher> teachers = teacherRepository.saveAll(getTeachers(kindergartenClasses));

        // TODO #5 유치원생 데이터 삽입
        List<Child> children = childRepository.saveAll(getChildren(parents, kindergartenClasses));

        // TODO #6 알림장 데이터 삽입
        noticeBoardRepository.saveAll(getNoticeBoards(kindergartenClasses.get(0)));

        // TODO #7 성장일지 데이터 삽입
        for (Child child : children) {
            diaryRepository.saveAll(getDiaries(child));
        }

        // TODO #8 투약 관련 데이터 삽입
        for (Child child : children) {
            dosageRepository.saveAll(getDosage(child));
        }

        // TODO #9 결석 관련 데이터 삽입
        for (Child child : children) {
            absentRepository.saveAll(getAbsent(child));
        }

        // TODO #10 학사 일정 추가
        for (Kindergarten kindergarten : kindergartens) {
            List<Schedule> schedules = getKindergartenSchedules(kindergarten);
            scheduleRepository.saveAll(schedules);
        }

        // TODO #11 선생님 일정 추가

        // TODO #12 부모님 일정 추가

        // TODO #13 앨범 데이터 추가
        List<Album> albums = getAlbums(children.get(0));
        albumRepository.saveAll(albums);

        // TODO #14 버스 데이터 추가
        List<Bus> busList = getBusList(kindergartens);
        List<Bus> saveBusList = busRepository.saveAll(busList);

        // TODO #15 버스 정류장 데이터 삽입
        for (int i = 2; i < busList.size(); i++) {
            List<BusStop> busStops = getBusStopList(saveBusList.get(i), i);
            busStopRepository.saveAll(busStops);
        }

        // TODO #16 버스 정류장에 아이 데이터 삽입 (1,2,3번 유치원만 해당)
        List<KindergartenClass> kindergartenClasses1 = kindergartenClassRepository.findByKindergarten(kindergartens.get(0));
        List<BusStop> busStops1 = busStopRepository.saveAll(getBusStopList(saveBusList.get(0), 0));
        saveBusStopChildInit(kindergartenClasses1, busStops1);
        List<KindergartenClass> kindergartenClasses2 = kindergartenClassRepository.findByKindergarten(kindergartens.get(1));
        List<BusStop> busStops2 = busStopRepository.saveAll(getBusStopList(saveBusList.get(1), 1));
        saveBusStopChildInit(kindergartenClasses2, busStops2);
        List<KindergartenClass> kindergartenClasses3 = kindergartenClassRepository.findByKindergarten(kindergartens.get(2));
        List<BusStop> busStops3 = busStopRepository.saveAll(getBusStopList(saveBusList.get(2), 2));
        saveBusStopChildInit(kindergartenClasses3, busStops3);
    }

    private void saveBusStopChildInit(List<KindergartenClass> kindergartenClasses, List<BusStop> busStops) {
        for (KindergartenClass kindergartenClass : kindergartenClasses) {
            List<Child> childList1 = childRepository.findByKindergartenClass(kindergartenClass);
            for (int i = 0; i < childList1.size(); i++) {
                BusStopChild busStopChild = new BusStopChild();
                busStopChild.setChild(childList1.get(i));
                busStopChild.setBusStop(busStops.get(i % busStops.size()));
                busStopChild.setBusBoardingStatus(BoardingStatus.T);
                busStopChildRepository.save(busStopChild);
            }
        }
    }

    private List<BusStop> getBusStopList(Bus bus, int i) {
        List<String> busStopNames = getBusStopNames();
        List<BusStop> busStops = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            BusStop busStop = new BusStop();
            busStop.setBusStopName(busStopNames.get(i * 3 + j));
            busStop.setBus(bus);
            busStops.add(busStop);
        }

        return busStops;
    }


    @Transactional
    protected void saveInitProfiles() {
        String[] imageFiles = {"child1.jpg", "child2.jpg", "child3.jpg", "child4.jpg", "child5.jpg", "child6.jpg", "child7.jpg", "child8.jpg", "child9.jpg", "child10.jpg", "child11.jpg", "child12.jpg", "child13.jpg"};
        String[] imageFiles2 = {"child21.jpg", "child22.jpg", "child23.jpg", "child24.jpg"};
        String[] imageFiles3 = {"child31.jpg", "child32.jpg", "child33.jpg", "child34.jpg"};

        uploadProfiles(imageFiles, initProfiles);
        uploadProfiles(imageFiles2, child2Profiles);
        uploadProfiles(imageFiles3, child3Profiles);
    }

    private List<Bus> getBusList(List<Kindergarten> kindergartens) {
        List<Bus> busList = new ArrayList<>();
        List<String> busNames = getBusNames();
        int i = 0;
        for (Kindergarten kindergarten : kindergartens) {
            Bus bus = new Bus();
            bus.setKindergarten(kindergarten);
            bus.setBusName(busNames.get(i++));
            busList.add(bus);
        }
        return busList;
    }

    private void uploadProfiles(String[] imageFiles, List<ImageDTO> profiles) {
        for (String imageFile : imageFiles) {
            File imageFileObj = new File(profileDir, imageFile);
            try {
                MultipartFile multipartFile = convertFileToMultipartFile(imageFileObj);
                ImageDTO imageDTO = imageService.storeFile(multipartFile);
                profiles.add(imageDTO);
            } catch (IOException e) {
                throw new RuntimeException("앨범 초기화 중 사진 저장 오류", e);
            }
        }
    }

    private List<Album> getAlbums(Child child) {
        List<Album> albums = new ArrayList<>();
        String[] albumNames = {"봄 소풍", "여름 캠프", "가을 운동회", "겨울 발표회"};
        for (String albumName : albumNames) {
            Album album = new Album();
            album.setAlbumName(albumName);
            album.setChild(child);

            Set<Image> images = new HashSet<>();
            for (ImageDTO imageDTO : initProfiles) {
                Image image = imageMapper.toEntity(imageDTO);
                images.add(image);
            }
            album.setImages(images);
            albums.add(album);
        }
        return albums;
    }

    private static List<Schedule> getKindergartenSchedules(Kindergarten kindergarten) {
        List<LocalDate> dates = Arrays.asList(
                LocalDate.of(2024, 3, 1), LocalDate.of(2024, 4, 5), LocalDate.of(2024, 5, 1), LocalDate.of(2024, 6, 21), LocalDate.of(2024, 8, 26), LocalDate.of(2024, 9, 5), LocalDate.of(2024, 10, 9),
                LocalDate.of(2024, 11, 11), LocalDate.of(2024, 12, 20), LocalDate.of(2025, 1, 2), LocalDate.of(2025, 2, 14), LocalDate.of(2025, 2, 28)
        );

        List<String> names = Arrays.asList(
                "신학기 시작", "식목일 행사", "가족 운동회", "여름방학 시작", "여름방학 종료", "추석 맞이 행사", "한글날 기념 행사",
                "농업인의 날 행사", "겨울방학 시작", "겨울방학 종료", "졸업식", "학기말 파티"
        );

        List<Schedule> schedules = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            Schedule schedule = new Schedule();
            schedule.setScheduleDate(dates.get(i));
            schedule.setScheduleName(names.get(i));
            schedule.setKindergarten(kindergarten);
            schedules.add(schedule);
        }
        return schedules;
    }

    private List<Kindergarten> getKindergartens() {
        return getKindergartenNames().stream()
                .map(name -> {
                    Kindergarten kindergarten = new Kindergarten();
                    kindergarten.setKindergartenName(name);
                    return kindergarten;
                })
                .collect(Collectors.toList());
    }

    private static List<KindergartenClass> getKindergartenClasses(List<Kindergarten> kindergartens) {
        return kindergartens.stream()
                .flatMap(kindergarten -> getKindergartenClassNames().stream()
                        .limit(3)
                        .map(className -> {
                            KindergartenClass kindergartenClass = new KindergartenClass();
                            kindergartenClass.setKindergartenId(kindergarten.getKindergartenId());
                            kindergartenClass.setKindergartenClassName(className);
                            return kindergartenClass;
                        }))
                .collect(Collectors.toList());
    }

    @Transactional
    protected void deleteAll() {
        // TODO #1 관계 테이블 클래스 정의 제대로 확인하기 (IdClass 또는 PK 생성)
        diaryService.deleteAllImageDiaries();
//        imageAlbumRepository.findAll().forEach(imageAlbum -> imageAlbumRepository.delete(imageAlbum));
        // TODO #2 위와 같은 이유로 Bus Table 초기화 시 삭제도 정의 제대로 하기
        busStopChildRepository.deleteAll();
        busStopRepository.deleteAll();
        busRepository.deleteAll();
        teacherNotificationRepository.deleteAll();
        parentNotificationRepository.deleteAll();
        meetingScheduleRepository.deleteAll();
        selectedMeetingRepository.deleteAll();
        meetingTimeRepository.deleteAll();
        albumRepository.deleteAll();
        diaryRepository.deleteAll();
        absentRepository.deleteAll();
        dosageRepository.deleteAll();
        scheduleRepository.deleteAll();
        noticeBoardRepository.deleteAll();
        childRepository.deleteAll();
        teacherRepository.deleteAll();
        parentRepository.deleteAll();
        imageRepository.deleteAll();
        kindergartenClassRepository.deleteAll();
        kindergartenRepository.deleteAll();
    }

    private static List<String> getKindergartenClassNames() {
        return Arrays.asList(
                "햇살반", "별빛반", "꿈나무반", "푸른숲반", "행복반",
                "사랑반", "하늘반", "바다반", "무지개반", "한울반",
                "동그라미반", "달님반", "솔잎반", "별나라반", "참나무반",
                "아름반", "솔바람반", "열매반", "다정반", "예쁜반"
        );
    }

    private List<String> getBusStopNames(){
        return Arrays.asList(
                "햇살정류장1", "햇살정류장2", "햇살정류장3", "햇살정류장4", "햇살정류장5",
                "별빛정류장1", "별빛정류장2", "별빛정류장3", "별빛정류장4", "별빛정류장5",
                "꿈나무정류장1", "꿈나무정류장2", "꿈나무정류장3", "꿈나무정류장4", "꿈나무정류장5",
                "푸른숲정류장1", "푸른숲정류장2", "푸른숲정류장3", "푸른숲정류장4", "푸른숲정류장5",
                "행복한정류장1", "행복한정류장2", "행복한정류장3", "행복한정류장4", "행복한정류장5",
                "사랑정류장1", "사랑정류장2", "사랑정류장3", "사랑정류장4", "사랑정류장5",
                "하늘정류장1", "하늘정류장2", "하늘정류장3", "하늘정류장4", "하늘정류장5",
                "바다정류장1", "바다정류장2", "바다정류장3", "바다정류장4", "바다정류장5",
                "무지개정류장1", "무지개정류장2", "무지개정류장3", "무지개정류장4", "무지개정류장5",
                "한울정류장1", "한울정류장2", "한울정류장3", "한울정류장4", "한울정류장5",
                "동그라미정류장1", "동그라미정류장2", "동그라미정류장3", "동그라미정류장4", "동그라미정류장5",
                "달님정류장1", "달님정류장2", "달님정류장3", "달님정류장4", "달님정류장5",
                "솔잎정류장1", "솔잎정류장2", "솔잎정류장3", "솔잎정류장4", "솔잎정류장5",
                "별나라정류장1", "별나라정류장2", "별나라정류장3", "별나라정류장4", "별나라정류장5",
                "참나무정류장1", "참나무정류장2", "참나무정류장3", "참나무정류장4", "참나무정류장5",
                "아름정류장1", "아름정류장2", "아름정류장3", "아름정류장4", "아름정류장5",
                "솔바람정류장1", "솔바람정류장2", "솔바람정류장3", "솔바람정류장4", "솔바람정류장5",
                "열매정류장1", "열매정류장2", "열매정류장3", "열매정류장4", "열매정류장5",
                "다정정류장1", "다정정류장2", "다정정류장3", "다정정류장4", "다정정류장5",
                "예쁜정류장1", "예쁜정류장2", "예쁜정류장3", "예쁜정류장4", "예쁜정류장5"
        );
    }

    private List<String> getBusNames(){
        return Arrays.asList(
                "햇살호", "별빛호", "꿈나무호", "푸른숲호", "행복한호", "사랑호", "하늘호",
                "바다호", "무지개호", "한울호", "동그라미호", "달님호", "솔잎호", "별나라호", "참나무호",
                "아름호", "솔바람호", "열매호", "다정호", "예쁜호"
        );
    }

    private List<String> getKindergartenNames() {
        return Arrays.asList("햇살 유치원", "별빛 유치원", "꿈나무 유치원", "푸른숲 유치원", "행복한 유치원", "사랑 유치원", "하늘 유치원"
                , "바다 유치원", "무지개 유치원", "한울 유치원", "동그라미 유치원", "달님 유치원", "솔잎 유치원", "별나라 유치원", "참나무 유치원",
                "아름 유치원", "솔바람 유치원", "열매 유치원", "다정 유치원", "예쁜 유치원"
        );
    }

    private List<Parent> getParents() {
        List<Parent> parents = new ArrayList<>();
        String[] usernames = {"parent1", "parent2", "parent3", "parent4", "parent5", "parent6", "parent7", "parent8", "parent9", "parent10", "parent11", "parent12", "parent13", "parent14", "parent15", "parent16", "parent17", "parent18"};
        String[] names = {"김철수", "이영희", "박민수", "최수영", "장준호", "강현우", "오세영", "윤지현", "정우성", "한지민", "홍길동", "임꺽정", "유재석", "강호동", "김혜수", "신민아", "송강호", "이정재"};
        String[] passwords = {"parent1", "parent2", "parent3", "parent4", "parent5", "parent6", "parent7", "parent8", "parent9", "parent10", "parent11", "parent12", "parent13", "parent14", "parent15", "parent16", "parent17", "parent18"};
        String[] nicknames = {"철수아빠", "영희엄마", "민수아빠", "수영엄마", "준호아빠", "현우엄마", "세영아빠", "지현엄마", "우성아빠", "지민엄마", "길동아빠", "꺽정엄마", "재석아빠", "호동엄마", "혜수아빠", "민아엄마", "강호아빠", "정재엄마"};
        String[] tels = {"010-1111-1111", "010-2222-2222", "010-3333-3333", "010-4444-4444", "010-5555-5555", "010-6666-6666", "010-7777-7777", "010-8888-8888", "010-9999-9999", "010-1010-1010", "010-1111-2222", "010-2222-3333", "010-3333-4444", "010-4444-5555", "010-5555-6666", "010-6666-7777", "010-7777-8888", "010-8888-9999"};
        String[] emails = {"user1@example.com", "user2@example.com", "user3@example.com", "user4@example.com", "user5@example.com", "user6@example.com", "user7@example.com", "user8@example.com", "user9@example.com", "user10@example.com", "user11@example.com", "user12@example.com", "user13@example.com", "user14@example.com", "user15@example.com", "user16@example.com", "user17@example.com", "user18@example.com"};

        for (int i = 0; i < usernames.length; i++) {
            Parent parent = new Parent();
            parent.setParentUsername(usernames[i]);
            parent.setParentName(names[i]);
            parent.setParentPassword(bCryptPasswordEncoder.encode(passwords[i]));
            parent.setParentNickname(nicknames[i]);
            parent.setParentTel(tels[i]);
            parent.setParentEmail(emails[i]);
            if (initProfiles.size() > i) {
                parent.setParentProfile(initProfiles.get(i).getPath());
            }
            parents.add(parent);
        }

        return parents;
    }

    private List<Teacher> getTeachers(List<KindergartenClass> kindergartenClasses) {
        List<Teacher> teachers = new ArrayList<>();
        String[] usernames = {"teacher1", "teacher2", "teacher3", "teacher4", "teacher5", "teacher6", "teacher7", "teacher8", "teacher9", "teacher10"};
        String[] names = {"최민호", "박지수", "이준혁", "김소연", "한동훈", "장수진", "홍길동", "서윤아", "조민기", "송지호"};
        String[] passwords = {"teacher1", "teacher2", "teacher3", "teacher4", "teacher5", "teacher6", "teacher7", "teacher8", "teacher9", "teacher10"};
        String[] nicknames = {"민호쌤", "지수쌤", "준혁쌤", "소연쌤", "동훈쌤", "수진쌤", "길동쌤", "윤아쌤", "민기쌤", "지호쌤"};
        String[] tels = {"010-1111-1111", "010-2222-2222", "010-3333-3333", "010-4444-4444", "010-5555-5555", "010-6666-6666", "010-7777-7777", "010-8888-8888", "010-9999-9999", "010-1010-1010"};
        String[] emails = {"user1@example.com", "user2@example.com", "user3@example.com", "user4@example.com", "user5@example.com", "user6@example.com", "user7@example.com", "user8@example.com", "user9@example.com", "user10@example.com"};

        for (int i = 0; i < usernames.length; i++) {
            Teacher teacher = new Teacher();
            teacher.setTeacherUsername(usernames[i]);
            teacher.setTeacherName(names[i]);
            teacher.setTeacherPassword(bCryptPasswordEncoder.encode(passwords[i]));
            teacher.setTeacherNickname(nicknames[i]);
            teacher.setTeacherTel(tels[i]);
            teacher.setTeacherEmail(emails[i]);
            teacher.setKindergartenClass(kindergartenClasses.get(i));
            teacher.setTeacherProfile(initProfiles.get(i).getPath());
            teachers.add(teacher);
        }

        return teachers;
    }

    private List<Child> getChildren(List<Parent> parents, List<KindergartenClass> kindergartenClasses) {
        List<Child> children = new ArrayList<>();
        String[] childNames = {"김하늘", "이민호", "박서연", "최우진", "장예빈", "강지후", "오수아", "윤도현", "정수지", "한지우"};
        Gender[] genders = {Gender.F, Gender.M, Gender.F, Gender.M, Gender.F, Gender.M, Gender.F, Gender.M, Gender.F, Gender.F};
        String[] births = {"2015-05-01", "2016-08-15", "2014-11-20", "2015-01-30", "2016-02-14", "2015-07-07", "2016-09-09", "2014-12-25", "2015-04-04", "2016-03-03"};

        for (int i = 0; i < childNames.length; i++) {
            Child child = new Child();
            child.setChildName(childNames[i]);
            child.setChildGender(genders[i]);
            child.setChildBirth(births[i]);
            child.setChildProfile(initProfiles.get(i).getPath());
            child.setParent(parents.get(i));
            child.setKindergartenClass(kindergartenClasses.get(0)); // 유치원 1반
            children.add(child);
        }

        String[] childNames2 = {"민지", "하니", "해린", "김용명"};
        Gender[] genders2 = {Gender.F, Gender.F, Gender.F, Gender.M};
        for (int i = 0; i < childNames2.length; i++) {
            Child child = new Child();
            child.setChildName(childNames2[i]);
            child.setChildGender(genders2[i]);
            child.setChildBirth(births[i]);
            child.setChildProfile(child2Profiles.get(i).getPath());
            child.setParent(parents.get(i + 10));
            child.setKindergartenClass(kindergartenClasses.get(1)); // 유치원 2반
            children.add(child);
        }

        String[] childNames3 = {"도하영", "이로", "하오", "윌리엄"};
        Gender[] genders3 = {Gender.F, Gender.F, Gender.M, Gender.M};
        for (int i = 0; i < childNames3.length; i++) {
            Child child = new Child();
            child.setChildName(childNames3[i]);
            child.setChildGender(genders3[i]);
            child.setChildBirth(births[i]);
            child.setChildProfile(child3Profiles.get(i).getPath());
            child.setParent(parents.get(i + 14));
            child.setKindergartenClass(kindergartenClasses.get(2)); // 유치원 3반
            children.add(child);
        }

        return children;
    }

    private List<NoticeBoard> getNoticeBoards(KindergartenClass kindergartenClass) {
        List<NoticeBoard> noticeBoards = new ArrayList<>();
        String[] titles = {
                "유치원 행사 안내", "여름 방학 일정", "가을 소풍 안내", "학부모 상담 일정", "어린이날 행사",
                "봄 운동회 공지", "겨울 방학 일정", "크리스마스 행사 안내", "새학기 일정", "체육 대회 안내",
                "유치원 생일 파티", "어린이 안전 교육", "가족 참여 행사", "과학 체험 행사", "미술 전시회",
                "음악 발표회", "여름 물놀이 행사", "책 읽기 대회", "문화 체험 행사", "영어 교육 프로그램"
        };
        String[] contents = {
                "다음 주에는 유치원에서 작은 행사가 열립니다. 많은 참여 부탁드립니다.",
                "이번 여름 방학 일정은 7월 20일부터 8월 20일까지입니다.",
                "가을 소풍은 10월 첫째 주에 진행됩니다. 자세한 사항은 추후 공지하겠습니다.",
                "학부모 상담 일정이 잡혔습니다. 각 반별로 시간표를 확인해 주세요.",
                "어린이날을 맞아 다양한 행사가 준비되어 있습니다. 많은 참여 바랍니다.",
                "봄 운동회가 열릴 예정입니다. 모든 아이들이 참여할 수 있도록 준비해주세요.",
                "겨울 방학 일정은 12월 24일부터 1월 10일까지입니다.",
                "크리스마스 행사로 다양한 활동이 준비되어 있습니다. 많은 참여 부탁드립니다.",
                "새학기 일정이 시작됩니다. 준비물과 일정을 확인해주세요.",
                "체육 대회가 열립니다. 모든 아이들이 함께 즐길 수 있는 시간이 될 것입니다.",
                "유치원 생일 파티가 열립니다. 생일을 맞은 아이들을 축하해주세요.",
                "어린이 안전 교육이 진행됩니다. 아이들의 안전을 위해 꼭 참여해주세요.",
                "가족 참여 행사가 열립니다. 부모님과 함께 즐거운 시간을 보내세요.",
                "과학 체험 행사가 열립니다. 아이들이 다양한 과학 활동을 경험할 수 있습니다.",
                "미술 전시회가 열립니다. 아이들의 창작 작품을 감상해보세요.",
                "음악 발표회가 열립니다. 아이들의 멋진 공연을 기대해주세요.",
                "여름 물놀이 행사가 열립니다. 시원한 물놀이를 즐기세요.",
                "책 읽기 대회가 열립니다. 아이들이 좋아하는 책을 읽고 발표하는 시간입니다.",
                "문화 체험 행사가 열립니다. 다양한 문화 활동을 체험할 수 있습니다.",
                "영어 교육 프로그램이 시작됩니다. 아이들이 영어를 재미있게 배울 수 있습니다."
        };

        for (int i = 0; i < titles.length; i++) {
            NoticeBoard noticeBoard = new NoticeBoard();
            noticeBoard.setNoticeBoardTitle(titles[i]);
            noticeBoard.setNoticeBoardContent(contents[i]);
            noticeBoard.setNoticeBoardDate(LocalDate.now().plusDays(i));
            noticeBoard.setKindergartenClass(kindergartenClass); // 유치원 반을 순환하며 할당
            noticeBoards.add(noticeBoard);
        }

        return noticeBoards;
    }

    public List<Diary> getDiaries(Child child) {
        List<Diary> diaries = new ArrayList<>();
        String[] contents = {
                "오늘은 유치원에서 재미있는 놀이를 했어요. 친구들과 함께 즐거운 시간을 보냈습니다.",
                "오늘은 유치원에서 그림을 그렸어요. 예쁜 꽃을 그려서 선생님께 칭찬을 받았습니다.",
                "오늘은 유치원에서 동화책을 읽었어요. 흥미로운 이야기를 들으며 상상력을 키웠습니다.",
                "오늘은 유치원에서 체육 활동을 했어요. 뛰어놀며 건강을 유지했습니다.",
                "오늘은 유치원에서 음악 시간을 가졌어요. 여러 가지 악기를 다루며 즐거운 시간을 보냈습니다.",
                "오늘은 유치원에서 과학 실험을 했어요. 신기한 실험을 통해 많은 것을 배웠습니다.",
                "오늘은 유치원에서 춤을 춰봤어요. 친구들과 함께 신나는 춤을 추며 즐거운 시간을 보냈습니다.",
                "오늘은 유치원에서 요리를 해봤어요. 간단한 요리를 만들어보며 요리사의 꿈을 키웠습니다.",
                "오늘은 유치원에서 자연 탐험을 했어요. 주변의 식물과 동물을 관찰하며 자연을 사랑하게 되었습니다.",
                "오늘은 유치원에서 숫자 공부를 했어요. 수학 문제를 풀며 논리적인 사고를 길렀습니다."
        };

        for (int i = 0; i < 30; i++) { // 100개의 테스트 데이터를 생성합니다.
            Diary diary = new Diary();
            diary.setDiaryDate(LocalDate.now().minusDays(i)); // 각 일지의 날짜를 다르게 설정합니다.
            diary.setDiaryContents(contents[i % contents.length]); // 다양한 내용을 순환하며 설정합니다.
            diary.setChild(child); // 주어진 child 객체를 설정합니다.
            List<ImageDTO> randomProfiles = new Random().ints(0, initProfiles.size())
                    .limit(3)
                    .mapToObj(initProfiles::get)
                    .toList();

            diary.setDiaryThumbnail(randomProfiles.get(0).getPath());

            Set<Image> imageSet = randomProfiles.stream()
                    .map(imageMapper::toEntity)
                    .collect(Collectors.toSet());

            diary.setImages(imageSet);
            diaries.add(diary);
        }

        return diaries;
    }

    public List<Absent> getAbsent(Child child) {
        List<Absent> absentList = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2024, 4, 1); // 시작 날짜 설정

        String[] reasons = {
                "병가", "휴가", "가족 행사", "의료 예약", "기타",
                "독감", "감기", "부상", "가족 응급 상황", "개인 사유",
                "결혼식", "장례식", "여행", "스포츠 행사", "기타"
        };

        String[] details = {
                "열과 기침", "가족 여행", "친척 결혼식", "의사 예약", "개인적인 이유",
                "심한 독감 증상", "감기 증상", "부상 치료", "가족 응급 상황 발생", "개인적인 용무",
                "친구 결혼식", "조부모 장례식", "해외 여행", "스포츠 대회 참가", "기타 이유"
        };

        Random random = new Random();
        int numAbsents = random.nextInt(15) + 1; // 1에서 15 사이의 랜덤 숫자
        Set<Integer> selectedIndices = new HashSet<>();

        while (selectedIndices.size() < numAbsents) {
            selectedIndices.add(random.nextInt(15));
        }

        int i = 0;
        for (int index : selectedIndices) {
            Absent absent = new Absent();
            absent.setAbsentStartdate(startDate.plusDays(i * 3));
            absent.setAbsentEnddate(startDate.plusDays(i * 3 + 1)); // 시작 날짜로부터 1일 뒤 종료
            absent.setAbsentReason(reasons[index]);
            absent.setAbsentDetails(details[index]);
            absent.setConfirmationStatus(ConfirmationStatus.T);
            absent.setChild(child);

            absentList.add(absent);
            i++;
        }

        return absentList;
    }

    public List<Dosage> getDosage(Child child) {
        List<Dosage> dosageList = new ArrayList<>();
        LocalDate startDate = LocalDate.of(2024, 3, 1); // 시작 날짜 설정

        String[] dosageNames = {
                "아세트아미노펜", "이부프로펜", "항생제", "비타민C", "알러지약",
                "감기약", "두통약", "소화제", "진통제", "항히스타민제",
                "해열제", "수면제", "진정제", "항생제", "기타"
        };

        String[] dosageVolumes = {
                "500mg", "200mg", "250mg", "1000mg", "50mg",
                "100mg", "400mg", "10ml", "500mg", "20mg",
                "300mg", "5mg", "2mg", "250mg", "100mg"
        };

        String[] dosageNums = {
                "1회", "2회", "1회", "1회", "3회",
                "2회", "1회", "1회", "1회", "1회",
                "1회", "1회", "1회", "1회", "1회"
        };

        String[] dosageTimes = {
                "아침", "점심", "저녁", "아침", "저녁",
                "점심", "저녁", "아침", "점심", "아침",
                "저녁", "점심", "아침", "저녁", "점심"
        };

        String[] dosageStores = {
                "냉장 보관", "서늘한 곳", "냉장 보관", "서늘한 곳", "실온 보관",
                "냉장 보관", "실온 보관", "서늘한 곳", "냉장 보관", "서늘한 곳",
                "실온 보관", "냉장 보관", "서늘한 곳", "냉장 보관", "실온 보관"
        };

        String[] dosageDetails = {
                "감기 증상 완화", "진통 및 소염", "세균 감염 치료", "면역력 강화", "알러지 완화",
                "콧물 및 재채기", "두통 완화", "소화 불량", "진통 완화", "알러지 반응 억제",
                "발열 완화", "수면 유도", "불안 완화", "세균 감염 치료", "기타 증상 완화"
        };

        Random random = new Random();
        int numDosages = random.nextInt(15) + 1; // 1에서 15 사이의 랜덤 숫자
        Set<Integer> selectedIndices = new HashSet<>();

        while (selectedIndices.size() < numDosages) {
            selectedIndices.add(random.nextInt(15));
        }

        int i = 0;
        for (int index : selectedIndices) {
            Dosage dosage = new Dosage();
            dosage.setDosageStartdate(startDate.plusDays(i * 3));
            dosage.setDosageEnddate(startDate.plusDays(i * 3 + 2)); // 시작 날짜로부터 2일 뒤 종료
            dosage.setDosageName(dosageNames[index]);
            dosage.setDosageVolume(dosageVolumes[index]);
            dosage.setDosageNum(dosageNums[index]);
            dosage.setDosageTime(dosageTimes[index]);
            dosage.setDosageStore(dosageStores[index]);
            dosage.setDosageDetails(dosageDetails[index]);
            dosage.setConfirmationStatus(ConfirmationStatus.T);
            dosage.setChild(child);

            dosageList.add(dosage);
            i++;
        }

        return dosageList;
    }

    private MultipartFile convertFileToMultipartFile(File file) throws IOException {
        Path path = Paths.get(file.getAbsolutePath());
        byte[] content;
        content = Files.readAllBytes(path);
        String contentType = Files.probeContentType(path);
        return new CustomMultipartFile(content, file.getName(), contentType);
    }

}
