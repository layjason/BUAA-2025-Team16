package com.example.lal.model.domain;


import lombok.Data;

import java.sql.Timestamp;


@Data
public class Resource {
    private int id;
    private int userId;
    private String title;
    private int subject;
    private Timestamp publishTime;
    private long size;
    private String content;
    private String path;
    private String imageUrl;
    private int downloadCount;
    private int authority;
    private String downloadUrl;
    private String fileName;
}
