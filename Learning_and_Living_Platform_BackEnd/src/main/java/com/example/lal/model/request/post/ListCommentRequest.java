package com.example.lal.model.request.post;

import lombok.Data;

@Data
public class ListCommentRequest {
    private int userId;
    private int postId;
    private int pageNum;
    private int cntInPage;
}
