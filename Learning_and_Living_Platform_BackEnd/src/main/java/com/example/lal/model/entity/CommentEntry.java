package com.example.lal.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class CommentEntry {
    private Integer userId;
    private String userName;
    private String profilePhotoUrl;
    private int userLevel;
    private String userLevelName;
    private int postId;
    private int id;
    private String content;
    private String imageUrl;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date publishTime;
    private int floor;
    private boolean canDelete;
    private List<ReplyEntry> replyList;
}
