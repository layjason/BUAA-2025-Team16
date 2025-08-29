package com.example.lal.model.request.resource;

import lombok.Data;

@Data
public class ListResourceByCategoryRequest {
    private int[] subjects;
    private int[] categories;
    private int cntInPage;
    private int pageNum;
}