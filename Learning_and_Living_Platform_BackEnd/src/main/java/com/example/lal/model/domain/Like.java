package com.example.lal.model.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Like {
    private int id;
    private int postId;
    private int userId;
    private Timestamp likeTime;
}
