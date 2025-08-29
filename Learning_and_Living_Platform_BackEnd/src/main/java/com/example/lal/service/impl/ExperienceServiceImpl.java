package com.example.lal.service.impl;

import com.example.lal.constant.ExpConstants;
import com.example.lal.mapper.ExperienceMapper;
import com.example.lal.service.ExperienceService;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Insert;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ExperienceServiceImpl implements ExperienceService {
    @Resource
    private ExperienceMapper experienceMapper;
    @Override
    public boolean changeExp(String curUserId, int changeExp) {
        if(!experienceMapper.getExperience(Integer.parseInt(curUserId))){
            return false;
        }
        int today = 0;
        if((today = experienceMapper.getDailyExp(Integer.parseInt(curUserId))) >= ExpConstants.EXP_DAILY_MAX){
            return false;
        }
        if(today + changeExp > ExpConstants.EXP_DAILY_MAX){
            changeExp = ExpConstants.EXP_DAILY_MAX - today;
        }

        return experienceMapper.updateExperience(curUserId, changeExp);
    }

    @Override
    public int getLevel(int userId){
        if(!experienceMapper.getExperience(userId)){
            return -1;
        }
        int exp = experienceMapper.readExperience(userId);
        if(exp < 0){
            return -1;
        } else if(exp < 64){
            return 0;
        } else if(exp < 128){
            return 1;
        } else if(exp < 256){
            return 2;
        } else if(exp < 512){
            return 3;
        } else if(exp < 1024){
            return 4;
        } else if(exp < 2048){
            return 5;
        } else if(exp < 4096){
            return 6;
        } else if(exp < 8192){
            return 7;
        } else if(exp < 16384){
            return 8;
        } else if(exp < 32768){
            return 9;
        } else {
            return 10;
        }
    }

    public String getLevelName(int level){
        if(level < 0){
            return null;
        } else if(level == 0){
            return "LV.0 新手小白";
        } else if(1 == level){
            return "LV.1 入门菜鸟";
        } else if(2 == level){
            return "LV.2 勤奋学徒";
        } else if(3 == level){
            return "LV.3 沉淀学士";
        } else if(4 == level){
            return "LV.4 探索能手";
        } else if(5 == level){
            return "LV.5 初现锋芒";
        } else if(6 == level){
            return "LV.6 高超大师";
        } else if(7 == level){
            return "LV.7 学富五车";
        } else if(8 == level){
            return "LV.8 知识巨人";
        } else if(9 == level){
            return "LV.9 传奇天才";
        } else {
            return "LV.10 神一般的存在";
        }
    }

    @Override
    public int readExp(int userId){
        if(!experienceMapper.getExperience(userId)){
            return -1;
        }
        return experienceMapper.readExperience(userId);
    }



    @Scheduled(cron = "0 0 0 * * *") // 每天凌晨执行
    public void resetDailyExp() {
        experienceMapper.resetDailyExp();
    }


}
