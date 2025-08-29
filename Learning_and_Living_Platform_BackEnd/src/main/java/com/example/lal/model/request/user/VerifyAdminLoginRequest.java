package com.example.lal.model.request.user;

import lombok.Data;

@Data
public class VerifyAdminLoginRequest {
    private String account;
    private String password;
}
