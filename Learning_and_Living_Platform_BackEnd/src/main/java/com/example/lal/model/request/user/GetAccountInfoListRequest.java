package com.example.lal.model.request.user;

import lombok.Data;

@Data
public class GetAccountInfoListRequest {
    private int cntInPage;
    private int pageNum;
}
