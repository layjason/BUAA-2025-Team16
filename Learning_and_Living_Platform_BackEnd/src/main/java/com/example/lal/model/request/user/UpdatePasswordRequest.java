package com.example.lal.model.request.user;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String userId;
    private String oldPassword;
    private String newPassword;
}
