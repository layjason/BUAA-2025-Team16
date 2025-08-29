package com.example.lal.model.request.user;

import lombok.Data;

@Data
public class VerifyRegisterRequest {
    private String email;
    private String password;
    private String salt;
}
