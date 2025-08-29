package com.example.lal.model.request.user;

import lombok.Data;

@Data
public class VerifyUserLoginRequest {
    private String idOrEmail;
    //private String email;
    private String password;
}
