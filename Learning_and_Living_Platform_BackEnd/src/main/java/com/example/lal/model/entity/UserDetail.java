package com.example.lal.model.entity;

import com.example.lal.model.domain.User;
import com.example.lal.model.domain.enums.Gender;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class UserDetail {
    private int id;
    private String name;
    private String email;
    private Gender gender;
    private java.sql.Date birthday;
    @JsonFormat(pattern="yyyy-MM-dd",timezone="GMT+8")
    private Timestamp registerTime;
    private String profilePhotoUrl;
    private int userLevel;
    private String userLevelName;
    private int exp;
    private int LogInNum;
}
