package com.example.lal.model.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Comment {
    private int id;
    private int postId;
    private int userId;
    private Timestamp publishTime;
    private String content;
    private String imageUrl;
    private int floor;
}
