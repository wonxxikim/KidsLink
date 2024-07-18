package com.ssafy.kidslink.application.teacher.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class JoinDTO {
    private String username;
    private String email;
    private String password;
    private String passwordConfirm;
    private String name;
    private String nickname;
    private String tel;
    private String kindergartenName;
    private String kindergartenClassName;
}
