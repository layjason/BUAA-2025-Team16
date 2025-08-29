package com.example.lal.service;

import org.springframework.stereotype.Service;

@Service
public interface ExperienceService {
    public boolean changeExp(String curUserId, int changeExp);

    int getLevel(int userId);
    int readExp(int userId);
    String getLevelName(int level);
}
