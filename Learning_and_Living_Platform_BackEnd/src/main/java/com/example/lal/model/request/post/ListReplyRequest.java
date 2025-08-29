package com.example.lal.model.request.post;

import lombok.Data;

@Data
public class ListReplyRequest {
    private int userId;
    private int commentId;
    private int cntInPage;
}
