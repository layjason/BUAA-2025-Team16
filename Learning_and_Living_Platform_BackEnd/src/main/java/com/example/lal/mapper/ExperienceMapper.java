package com.example.lal.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface ExperienceMapper {
    @Update("UPDATE Experience e SET e.exp = (e.exp+#{changeNumber}), e.dailyExp = (e.dailyExp+#{changeNumber}) WHERE e.id=#{curUserId};")
    boolean updateExperience(String curUserId, int changeNumber);

    @Select("Select COUNT(*) FROM Experience WHERE id=#{curUserID};")
    boolean getExperience(int curUserId);

    @Select("SELECT exp FROM Experience WHERE id=#{userId};")
    int readExperience(int userId);

    @Update("UPDATE Experience SET dailyExp=0;")
    void resetDailyExp();

    @Select("SELECT dailyExp FROM Experience WHERE id=#{userId};")
    int getDailyExp(int userId);
}
