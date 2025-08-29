package com.example.lal.service.impl;

import com.example.lal.mapper.DownloadHistoryMapper;
import com.example.lal.model.entity.DownloadHistoryEntry;
import com.example.lal.model.entity.UserResourceMap;
import com.example.lal.service.DownloadHistoryService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DownloadHistoryServiceImpl implements DownloadHistoryService {
    @Resource
    private DownloadHistoryMapper downloadHistoryMapper;
    @Override
    public int getDownloadsByTime(LocalDateTime startTime, LocalDateTime endTime){
        return downloadHistoryMapper.getDownloadsByTime(startTime,endTime);
    }
    @Override
    public boolean addDownloadHistory(String userId, String resourceId,String resourceTitle, String fileName){
        DownloadHistoryEntry downloadHistoryEntry = new DownloadHistoryEntry();
        downloadHistoryEntry.setResourceId(Integer.parseInt(resourceId));
        downloadHistoryEntry.setUserId(Integer.parseInt(userId));
        downloadHistoryEntry.setDownloadTime(new java.sql.Date(System.currentTimeMillis()));
        downloadHistoryEntry.setTitle(resourceTitle);
        downloadHistoryEntry.setFileName(fileName);
        return downloadHistoryMapper.createDownloadHistory(downloadHistoryEntry);
    }
    @Override
    public List<DownloadHistoryEntry> listDownloadHistoryByUserId(String userId){
        return downloadHistoryMapper.readDownloadHistoryByUserId(Integer.parseInt(userId));
    }
    @Override
    public List<Integer> listAllResourceId(){
        return downloadHistoryMapper.readAllResourceId();
    }
    @Override
    public List<Integer> listDownloadHistoryUserIdByResourceId(Integer resourceId){
        return downloadHistoryMapper.readDownloadHistoryByResourceId(resourceId);
    }
    @Override
    public Map<Integer, Integer> getUserResourceMap(){
        List<UserResourceMap> userResourceMapList = downloadHistoryMapper.getUserResourceMap();
        Map<Integer, Integer> userResourceMap = new java.util.HashMap<>();
        for(UserResourceMap userResourceMapEntry : userResourceMapList){
            userResourceMap.put(userResourceMapEntry.getUserId(), userResourceMapEntry.getResourceCnt());
        }
        return userResourceMap;
    }
}
