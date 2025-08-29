package com.example.lal.model.domain;

import lombok.Data;

import java.sql.Timestamp;
import java.util.Date;

@Data
public class Log {
    private int id;
    private int userId;
    private Date loginTime;
    private Date logoutTime;
}
