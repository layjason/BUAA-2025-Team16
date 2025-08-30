package com.example.lal.model.entity;

import lombok.Data;

@Data
public class UserResourceMap {
    private int userId;
    private int resourceCnt;

    // 添加构造方法
    public UserResourceMap(int userId, int resourceCnt) {
        this.userId = userId;
        this.resourceCnt = resourceCnt;
    }
}
