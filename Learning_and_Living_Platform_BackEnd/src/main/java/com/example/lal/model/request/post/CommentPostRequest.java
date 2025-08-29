package com.example.lal.model.request.post;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Data
public class CommentPostRequest {
    private int postId;
    private int userId;
    private List<String> imageUrl;
    private Date publishTime;
    private String content;
}
