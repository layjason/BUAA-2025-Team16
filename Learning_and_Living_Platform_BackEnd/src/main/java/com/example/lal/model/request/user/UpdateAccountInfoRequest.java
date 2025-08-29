package com.example.lal.model.request.user;

import com.example.lal.model.domain.enums.Gender;
import lombok.Data;

import java.sql.Date;


@Data
public class UpdateAccountInfoRequest {
    private int id;
    private String name;
    private String email;
    private Gender gender;
    private Date birthday;
    private String profilePhotoUrl;
}
