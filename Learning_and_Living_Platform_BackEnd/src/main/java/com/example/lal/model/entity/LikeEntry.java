package com.example.lal.model.entity;

import lombok.Data;

import java.util.Date;

@Data
public class LikeEntry {
    private int id;
    private int postId;
    private int userId;
    private Date likeTime;
}
