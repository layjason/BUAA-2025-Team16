package com.example.lal.model.request.resource;

import lombok.Data;

@Data
public class GetDownloadHistoryRequest {
    private int cntInPage;
    private int pageNum;
}
