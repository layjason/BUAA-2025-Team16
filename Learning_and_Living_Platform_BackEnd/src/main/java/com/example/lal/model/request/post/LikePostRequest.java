package com.example.lal.model.request.post;

import lombok.Data;

@Data
public class LikePostRequest {
    private int postId;
    private Boolean isLike;
}
