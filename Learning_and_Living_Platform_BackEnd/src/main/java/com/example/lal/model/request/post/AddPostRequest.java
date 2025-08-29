package com.example.lal.model.request.post;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class AddPostRequest {
    private String title;
    private String content;
    private Date postTime;
    private int authority;
    private List<String> images;
    private String thumbnails;
}
