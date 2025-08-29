package com.example.lal.service.impl;

import com.example.lal.mapper.LogMapper;
import com.example.lal.model.domain.Log;
import com.example.lal.service.LogService;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class LogServiceImpl implements LogService {
    @Resource
    LogMapper logMapper;
    @Override
    public boolean addLog(String curUserId) {
        return logMapper.addLog(curUserId);
    }
    @Override
    public void deleteLogByUserId(String id) {
        logMapper.deleteLogByUserId(id);
    }
    @Override
    public void userLogout(int userId){
        //获取当前时间
        logMapper.addLogoutTime(userId);
    }
    @Override
    public Log getLastLog(String userId){
        return logMapper.getLastLogByUserId(userId);
    }
}
