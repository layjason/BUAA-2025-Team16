package com.example.lal.model.request.resource;

import lombok.Data;

@Data
public class UploadResourceRequest {
    private String title;
    private int subject;
    private int[] categories;
    //private Date publishTime;感觉不需要这个了，上传资源时直接获取当前时间就可以了吧
    private String content;
    private String imagePath;
    private String filePath;
    private String fileName;
    private long size;
}
