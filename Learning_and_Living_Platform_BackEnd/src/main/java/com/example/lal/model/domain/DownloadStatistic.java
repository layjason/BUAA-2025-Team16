package com.example.lal.model.domain;

import lombok.Data;

import java.util.Date;

@Data
public class DownloadStatistic {
    int subject;
    int category;
    int count;
    Date day;
}
