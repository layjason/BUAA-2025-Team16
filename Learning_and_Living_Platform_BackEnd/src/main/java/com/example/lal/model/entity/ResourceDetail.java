package com.example.lal.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class ResourceDetail {
    private int id;
    private String userId;
    private String username;
    private String profilePhotoUrl;
    private String title;
    private int subject;
    private int[] categories;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date publishTime;
    private String content;
    private String fileName;
    private String imageUrl;
    private String path;
    private int downloadCount;
    private int size;
    private boolean canDelete;
    private boolean canModify;
    private boolean canDownload;
}
