package com.example.lal.mapper;

import com.example.lal.model.entity.LikeEntry;
import org.apache.ibatis.annotations.*;

import java.util.Date;
import java.util.List;

@Mapper
public interface LikeMapper {

    @Insert("INSERT INTO `Like`(postId, userId, likeTime) "+
    "VALUES (#{postId}, #{userId}, #{likeTime});")
    boolean createLike(int postId, int userId, Date likeTime);

    @Delete("DELETE FROM `Like` WHERE postId=#{postId} AND userId=#{userId};")
    boolean deleteLike(int postId, int userId);

    @Select("SELECT COUNT(*) FROM `Like` WHERE postId=#{postId} AND userId=#{userId};")
    boolean isLike(int postId, int userId);


}
