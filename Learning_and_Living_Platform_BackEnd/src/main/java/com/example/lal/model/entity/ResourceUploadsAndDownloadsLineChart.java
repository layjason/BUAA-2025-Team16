package com.example.lal.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
@Data

public class ResourceUploadsAndDownloadsLineChart {
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    Date[] days;
    int[] downloads;
    int[] uploads;
}
