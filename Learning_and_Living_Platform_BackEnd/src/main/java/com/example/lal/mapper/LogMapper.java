package com.example.lal.mapper;

import com.example.lal.model.domain.Log;
import org.apache.ibatis.annotations.*;

@Mapper
public interface LogMapper {
    @Insert("insert into Log (userId,loginTime) values (#{curUserId},NOW())")
    boolean addLog(String curUserId);
    @Delete("delete from Log where userId=#{id}")
    void deleteLogByUserId(String id);

    @Update("UPDATE Log " +
            "SET logoutTime = NOW() " +
            "WHERE id IN ( " +
            "  SELECT temp.id " +
            "  FROM ( " +
            "    SELECT id " +
            "    FROM Log " +
            "    WHERE userId = #{userId} " +
            "    ORDER BY loginTime DESC " +
            "    LIMIT 1 " +
            "  ) AS temp " +
            ")")
    void addLogoutTime(int userId);
    @Select("SELECT * FROM Log WHERE userId = #{userId} ORDER BY loginTime DESC LIMIT 1")
    Log getLastLogByUserId(@Param("userId") String userId);

}
