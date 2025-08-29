package com.example.lal.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class PostSummary {
    private int id;
    private Integer userId;
    private String userName;
    private String profilePhotoUrl;
    private int authority;
    private String userLevelName;
    private String title;
    private int likeCount;
    private int browseCount;
    private long hotPoint;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
    private boolean canDelete;
}
