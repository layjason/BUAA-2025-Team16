package com.example.lal.model.request.resource;

import lombok.Data;

@Data
public class UpdateResourceRequest {
    private String id;
    private String title;
    private int subject;
    private int[] categories;
    private String content;
}
