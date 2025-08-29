package com.example.lal.mapper;

import com.example.lal.model.entity.DownloadHistoryEntry;
import com.example.lal.model.entity.UserResourceMap;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface DownloadHistoryMapper {
    @Insert("INSERT INTO DownloadHistory(userId, resourceId, downloadTime,title,fileName) "+
    "VALUES (#{userId}, #{resourceId}, #{downloadTime},#{title},#{fileName});")
    boolean createDownloadHistory(DownloadHistoryEntry downloadHistory);

    //查询最近50条的
    @Select("SELECT * FROM DownloadHistory " +
            "WHERE userId=#{userId} " +
            "ORDER BY id DESC " +
            "LIMIT 50;")
    List<DownloadHistoryEntry> readDownloadHistoryByUserId(int userId);

    @Select("SELECT DISTINCT resourceId " +
            "FROM DownloadHistory " +
            "WHERE resourceId IS NOT NULL ")
    List<Integer> readAllResourceId();

    @Select("SELECT DISTINCT userId FROM DownloadHistory " +
            "WHERE resourceId=#{resourceId} " +
            "AND userId IS NOT NULL;")
    List<Integer> readDownloadHistoryByResourceId(Integer resourceId);

    @Select("SELECT count(*) as resourceCnt,a.userId " +
            "from (SELECT userId,count(*) FROM DownloadHistory " +
            "            WHERE userId IS NOT NULL " +
            "            GROUP BY userId,resourceId )as a " +
            "GROUP BY a.userId  ")
    List<UserResourceMap> getUserResourceMap();

    @Select("SELECT COUNT(*) AS count " +
            "FROM DownloadHistory " +
            "WHERE downloadTime >= #{startTime} AND downloadTime <= #{endTime}")
    int getDownloadsByTime(LocalDateTime startTime, LocalDateTime endTime);
}
