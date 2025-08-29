package com.example.lal.model.request.post;

import lombok.Data;

@Data
public class ListUserPostRequest {
    private int cntInPage;
    private int pageNum;
    private int userId;
}
