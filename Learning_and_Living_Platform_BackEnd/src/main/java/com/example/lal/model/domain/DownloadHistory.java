package com.example.lal.model.domain;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class DownloadHistory {
    private int id;
    private int userId;
    private int resourceId;
    private Timestamp downloadTime;
}
