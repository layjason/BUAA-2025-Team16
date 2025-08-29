package com.example.lal.model.request.post;

import lombok.Data;

@Data
public class ListPostRequest {
    private int cntInPage;
    private int pageNum;
    private int mode;
}
