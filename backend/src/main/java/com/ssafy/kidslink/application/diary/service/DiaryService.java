package com.ssafy.kidslink.application.diary.service;

import com.ssafy.kidslink.application.child.domain.Child;
import com.ssafy.kidslink.application.child.repository.ChildRepository;
import com.ssafy.kidslink.application.diary.domain.Diary;
import com.ssafy.kidslink.application.diary.domain.ImageDiary;
import com.ssafy.kidslink.application.diary.dto.DiaryDTO;
import com.ssafy.kidslink.application.diary.dto.DiaryRequestDTO;
import com.ssafy.kidslink.application.diary.mapper.DiaryMapper;
import com.ssafy.kidslink.application.diary.repository.DiaryRepository;
import com.ssafy.kidslink.application.diary.repository.ImageDiaryRepository;
import com.ssafy.kidslink.application.image.dto.ImageDTO;
import com.ssafy.kidslink.application.image.mapper.ImageMapper;
import com.ssafy.kidslink.application.image.service.ImageService;
import com.ssafy.kidslink.application.notification.domain.ParentNotification;
import com.ssafy.kidslink.application.notification.respository.ParentNotificationRepository;
import com.ssafy.kidslink.application.parent.domain.Parent;
import com.ssafy.kidslink.application.parent.repository.ParentRepository;
import com.ssafy.kidslink.application.teacher.repository.TeacherRepository;
import com.ssafy.kidslink.common.enums.NotificationCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiaryService {

    private final ChildRepository childRepository;
    private final DiaryRepository diaryRepository;
    private final ParentRepository parentRepository;
    private final ParentNotificationRepository parentNotificationRepository;

    private final ImageService imageService;
    private final ImageDiaryRepository imageDiaryRepository;
    private final ImageMapper imageMapper;
    private final TeacherRepository teacherRepository;
    private final DiaryMapper diaryMapper;

    public void createDiary(int childId, String teacherUsername, DiaryRequestDTO request) {
        Diary diary = new Diary();

        List<MultipartFile> files = request.getFiles();
        List<ImageDTO> images = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    images.add(imageService.storeFile(file));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        diary.setDiaryContents(request.getContent());
        diary.setDiaryDate(LocalDate.parse(request.getDiaryDate()));
        diary.setChild(childRepository.findById(childId).orElseThrow());

        if (!images.isEmpty()) {
            diary.setDiaryThumbnail(images.get(0).getPath());
            Diary savedDiary = diaryRepository.save(diary);
            for (ImageDTO image : images) {
                ImageDiary imageDiary = new ImageDiary();
                imageDiary.setDiary(savedDiary);
                imageDiary.setImage(imageService.getImageById(image.getImageId()));

                imageDiaryRepository.save(imageDiary);
            }
        } else {
            diaryRepository.save(diary);
        }

        Child child = diary.getChild();
        Parent parent = child.getParent();

        ParentNotification parentNotification = new ParentNotification();
        parentNotification.setCode(NotificationCode.NOTICE);
        parentNotification.setParentNotificationDate(LocalDate.now());
        parentNotification.setParentNotificationText("우리 아이의 성장일지가 등록되었습니다.");
        parentNotification.setParent(parent);

        parentNotificationRepository.save(parentNotification);
    }

    public List<DiaryDTO> getAllDiary(int childId) {
        List<Diary> diaries = diaryRepository.findByChildChildId(childId);
        List<DiaryDTO> diaryDTOs = new ArrayList<>();
        for (Diary diary : diaries) {
            diaryDTOs.add(diaryMapper.toDTO(diary));
        }
        return diaryDTOs;
    }

    public DiaryDTO getDiary(int diaryId) {
        return diaryRepository.findById(diaryId).map(diaryMapper::toDTO).orElseThrow();
    }

    @Transactional
    public void deleteAllImageDiaries() {
        try {
            List<ImageDiary> imageDiaries = imageDiaryRepository.findAll();
            imageDiaryRepository.deleteAll(imageDiaries);
            imageDiaryRepository.flush();
        } catch (ObjectOptimisticLockingFailureException e) {
            // 동시성 문제 해결을 위한 예외 처리
            System.err.println("Optimistic locking failure: " + e.getMessage());
        }
    }
}
