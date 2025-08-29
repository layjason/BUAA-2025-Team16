package com.example.lal.service;

import com.example.lal.model.domain.Log;
import org.springframework.stereotype.Service;

@Service
public interface LogService {
    public boolean addLog(String log);

    void deleteLogByUserId(String id);

    void userLogout(int id);

    Log getLastLog(String userId);
}
