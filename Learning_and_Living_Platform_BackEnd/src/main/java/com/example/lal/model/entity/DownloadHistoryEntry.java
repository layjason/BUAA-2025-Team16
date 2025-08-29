package com.example.lal.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class DownloadHistoryEntry {
    private int id;
    private int resourceId;
    private int userId;
    private String title;
    private String fileName;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date downloadTime;
}