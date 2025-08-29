package com.example.lal.model.entity;

import lombok.Data;

@Data
public class ResourceSummary {
    //头像，昵称，id，然后资源的id，标题，内容，学科，类别
    private int id;
    private String userId;
    private String userName;
    private String profilePhotoUrl;
    private String title;
    private String content;
    private int subject;
    private int[] categories;
    private int downloadCount;

    private boolean canDelete;
}
