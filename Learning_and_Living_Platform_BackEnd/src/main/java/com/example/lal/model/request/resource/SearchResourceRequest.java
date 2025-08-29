package com.example.lal.model.request.resource;

import lombok.Data;

@Data
public class SearchResourceRequest {
    String keywords;
    int[] subjects;
    int[] categories;
    int cntInPage;
    int pageNum;
}
