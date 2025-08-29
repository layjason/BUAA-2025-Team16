package com.example.lal.model.request.post;

import lombok.Data;

@Data
public class DeletePostRequest {
    private int userId;
    private int postId;
    private Integer pageNum;
}
