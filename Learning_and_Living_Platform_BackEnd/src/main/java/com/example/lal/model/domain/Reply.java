package com.example.lal.model.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Reply {
    private int id;
    private int postId;
    private int commentId;
    private int userId;
    private Timestamp publishTime;
    private String content;
}
