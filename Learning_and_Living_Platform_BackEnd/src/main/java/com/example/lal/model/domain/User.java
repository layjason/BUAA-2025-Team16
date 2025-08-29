package com.example.lal.model.domain;

import com.example.lal.model.domain.enums.Gender;
import lombok.Data;

import java.sql.Date;

@Data
public class User {
    //id INT AUTO_INCREMENT PRIMARY KEY,
    //	`name` VARCHAR(20),
    //	`password` VARCHAR(255) NOT NULL,
    //	email VARCHAR(40) NOT NULL,
    //	gender ENUM('Male', 'Female', 'Helicopter') DEFAULT 'Helicopter',
    //	birthday DATE,
    //	salt VARCHAR(20) NOT NULL,
    //	registerTime DATE,
    //	profilePhotoUrl VARCHAR(255),
    //	LogInNum INT DEFAULT 0
    //) AUTO_INCREMENT = 10000001;
    private int id;
    private String name;
    private String password;
    private String email;
    private Gender gender;
    private Date birthday;
    private Date registerTime;
    private String profilePhotoUrl;
    private int LogInNum;

}
