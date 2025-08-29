package com.example.lal.model.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class Post {
    private int id;
    private String title;
    private int userId;
    private String content;
    private int floorCount;
    private Timestamp postTime;
    private String thumbnailUrls;
    private String imageUrls;
    private int likeCount;
    private int authority;
    private int browseCount;

    public int getHot() {
        return likeCount * 10 + browseCount;
    }
}
