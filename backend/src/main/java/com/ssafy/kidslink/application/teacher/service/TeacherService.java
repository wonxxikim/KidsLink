package com.ssafy.kidslink.application.teacher.service;

import com.ssafy.kidslink.application.child.repository.ChildRepository;
import com.ssafy.kidslink.application.image.service.ImageService;
import com.ssafy.kidslink.application.kindergarten.domain.KindergartenClass;
import com.ssafy.kidslink.application.kindergarten.repository.KindergartenClassRepository;
import com.ssafy.kidslink.application.teacher.domain.Teacher;
import com.ssafy.kidslink.application.teacher.dto.JoinDTO;
import com.ssafy.kidslink.application.teacher.dto.TeacherDTO;
import com.ssafy.kidslink.application.teacher.mapper.TeacherMapper;
import com.ssafy.kidslink.application.teacher.repository.TeacherRepository;
import com.ssafy.kidslink.common.exception.PasswordMismatchException;
import com.ssafy.kidslink.common.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeacherService {
    private final TeacherRepository teacherRepository;
    private final KindergartenClassRepository kindergartenClassRepository;
    private final ChildRepository childRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageService imageService;

    private final JWTUtil jwtUtil;
    private final TeacherMapper teacherMapper;
    // 선생님 회원가입
    public void joinProcess(JoinDTO joinDTO){
        if(!joinDTO.getPassword().equals(joinDTO.getPasswordConfirm())){
            throw new PasswordMismatchException("비밀번호와 비밀번호 확인이 다릅니다.");
        }

        Teacher teacher = new Teacher();
        teacher.setTeacherName(joinDTO.getName());
        teacher.setTeacherEmail(joinDTO.getEmail());
        teacher.setTeacherNickname(joinDTO.getNickname());
        teacher.setTeacherTel(joinDTO.getTel());
        teacher.setTeacherUsername(joinDTO.getUsername());
        teacher.setTeacherPassword(bCryptPasswordEncoder.encode(joinDTO.getPassword()));
        if (joinDTO.getProfile() != null) {
            try {
                teacher.setTeacherProfile(imageService.storeFile(joinDTO.getProfile()).getPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        KindergartenClass kindergartenClass = kindergartenClassRepository.findByKindergartenKindergartenNameAndKindergartenClassName(
                joinDTO.getKindergartenName(), joinDTO.getKindergartenClassName()
        );
        teacher.setKindergartenClass(kindergartenClass);

        teacherRepository.save(teacher);
    }

    public TeacherDTO detailProcess(String teacherUsername){
        Teacher teacher = teacherRepository.findByTeacherUsername(teacherUsername);
        return teacherMapper.toDTO(teacher);
    }

    public int getKindergartenClassIdByTeacherUsername(String username) {
        return teacherRepository.findByTeacherUsername(username).getKindergartenClass().getKindergartenClassId();
    }

}
