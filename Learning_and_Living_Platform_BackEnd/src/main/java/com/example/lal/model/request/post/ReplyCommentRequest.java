package com.example.lal.model.request.post;

import lombok.Data;

import java.util.Date;

@Data
public class ReplyCommentRequest {
    private int userId;
    private int postId;
    private int commentId;
    private Date publishTime;
    private String content;
}
