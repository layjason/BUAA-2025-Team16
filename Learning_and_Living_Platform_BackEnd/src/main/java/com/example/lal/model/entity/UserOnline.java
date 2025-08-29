package com.example.lal.model.entity;

import com.example.lal.model.domain.User;
import lombok.Data;

@Data
public class UserOnline extends User {
    String token;
}
