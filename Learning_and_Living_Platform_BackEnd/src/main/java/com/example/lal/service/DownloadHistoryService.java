package com.example.lal.service;

import com.example.lal.model.entity.DownloadHistoryEntry;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface DownloadHistoryService {
    int getDownloadsByTime(LocalDateTime startTime, LocalDateTime endTime);

    boolean addDownloadHistory(String userId, String resourceId, String resourceTitle, String fileName);

    List<DownloadHistoryEntry> listDownloadHistoryByUserId(String userId);

    List<Integer> listAllResourceId();

    List<Integer> listDownloadHistoryUserIdByResourceId(Integer resourceId);

    Map<Integer, Integer> getUserResourceMap();
}
