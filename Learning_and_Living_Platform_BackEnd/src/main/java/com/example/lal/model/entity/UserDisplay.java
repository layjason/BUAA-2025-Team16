package com.example.lal.model.entity;

import com.example.lal.model.domain.User;
import lombok.Data;

@Data
public class UserDisplay {
    private String id;
    private String name;
    private String profilePhotoUrl;
    private String token;
    public UserDisplay(User user, String token){
        this.id = String.valueOf(user.getId());
        this.name = user.getName();
        this.profilePhotoUrl = user.getProfilePhotoUrl();
        this.token = token;
    }


}
