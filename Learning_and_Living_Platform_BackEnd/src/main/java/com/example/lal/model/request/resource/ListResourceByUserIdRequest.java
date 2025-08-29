package com.example.lal.model.request.resource;

import lombok.Data;

@Data
public class ListResourceByUserIdRequest {
    int cntInPage;
    int pageNum;
    String userId;
}
