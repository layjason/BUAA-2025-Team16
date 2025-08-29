package com.example.lal.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Data
@Component
public class PostDetail {
    private int id;
    private Integer userId;
    private String userName;
    private int userLevel;
    private String userLevelName;
    private String profilePhotoUrl;
    private String title;
    private String content;
    private int likeCount;
    private int browseCount;
//    private String thumbnailUrls;
    private String imageurls;
    private List<String> imageUrlList;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date postTime;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
    private Page<CommentEntry> commentList;
    private int authority;
    private long hotPoint;
    private boolean canDelete;
    private boolean like = false;
    private boolean canOperate;
}
