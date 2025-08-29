package com.example.lal.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ReplyEntry {
    private int id;

    private Integer userId;
    private String userName;
    private String profilePhotoUrl;
    private int userLevel;
    private String userLevelName;

    private int postId;
    private int commentId;

    private String content;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date publishTime;
    private boolean canDelete;
}
