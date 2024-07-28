package com.ssafy.kidslink.application.document.service;

import com.ssafy.kidslink.application.child.domain.Child;
import com.ssafy.kidslink.application.document.domain.Dosage;
import com.ssafy.kidslink.application.document.dto.DosageDTO;
import com.ssafy.kidslink.application.document.mapper.DosageMapper;
import com.ssafy.kidslink.application.document.repository.DosageRepository;
import com.ssafy.kidslink.application.notification.domain.TeacherNotification;
import com.ssafy.kidslink.application.notification.respository.TeacherNotificationRepository;
import com.ssafy.kidslink.application.parent.domain.Parent;
import com.ssafy.kidslink.application.parent.repository.ParentRepository;
import com.ssafy.kidslink.application.teacher.repository.TeacherRepository;
import com.ssafy.kidslink.common.enums.ConfirmationStatus;
import com.ssafy.kidslink.common.enums.NotificationCode;
import com.ssafy.kidslink.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class DosageService {
    private final ParentRepository parentRepository;
    private final DosageMapper dosageMapper;
    private final DosageRepository dosageRepository;
    private final TeacherRepository teacherRepository;
    private final TeacherNotificationRepository teacherNotificationRepository;

    private Parent getParentByUsername(String parentUsername) {
        return parentRepository.findByParentUsername(parentUsername);
    }
    public DosageDTO createDosage(String parentUsername, DosageDTO dosageDTO) {
        Parent parent = getParentByUsername(parentUsername);
        Child child = parent.getChildren().iterator().next(); //부모의 첫번째 자식반환. 자식 여러명 확장시 수정필요
        Dosage dosage = dosageMapper.toEntity(dosageDTO, child);
        Dosage savedDosage = dosageRepository.save(dosage);

        TeacherNotification teacherNotification = new TeacherNotification();
        teacherNotification.setCode(NotificationCode.DOCUMENT);
        teacherNotification.setTeacherNotificationDate(LocalDate.now());
        teacherNotification.setTeacher(teacherRepository.findByKindergartenClass(parent.getChildren().iterator().next().getKindergartenClass()));
        teacherNotification.setTeacherNotificationText("새로운 투약 서류가 등록되었습니다.");
        teacherNotificationRepository.save(teacherNotification);

        return dosageMapper.toDTO(savedDosage);
    }
    public List<DosageDTO> getAllDosages() {
        List<Dosage> dosageList = dosageRepository.findAll();
        List<DosageDTO> dosageDTOList = new ArrayList<>();

        for (Dosage dosage : dosageList) {
            DosageDTO dto = dosageMapper.toDTO(dosage);
            dosageDTOList.add(dto);
        }

        return dosageDTOList;
    }
    public DosageDTO getDosageByDosageId(int dosageId) {
        Optional<Dosage> dosage = dosageRepository.findById(dosageId);
        if (dosage.isPresent()) {
            return dosageMapper.toDTO(dosage.get());
        } else {
            throw new ResourceNotFoundException("Dosage not found with id " + dosageId);
        }
    }

    public void updateDosage(int dosageId) {
        Optional<Dosage> dosageOptional = dosageRepository.findById(dosageId);

        if (dosageOptional.isPresent()) {
            Dosage dosage = dosageOptional.get();
            dosage.setConfirmationStatus(ConfirmationStatus.T);
            dosageRepository.save(dosage);
        } else {
            throw new RuntimeException("Dosage not found with id " + dosageId);
        }
    }
}
