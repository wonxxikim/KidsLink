package com.ssafy.kidslink.application.notification.domain;

import com.ssafy.kidslink.application.teacher.domain.Teacher;
import com.ssafy.kidslink.common.enums.NotificationCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "teacher_notification")
public class TeacherNotification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "teacher_notification_id")
    private Integer teacherNotificationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(name = "teacher_notification_code")
    private NotificationCode code;

    @Column(name = "teacher_notification_text")
    private String teacherNotificationText;

    @Column(name = "teacher_notification_date")
    private Date teacherNotificationDate;

}