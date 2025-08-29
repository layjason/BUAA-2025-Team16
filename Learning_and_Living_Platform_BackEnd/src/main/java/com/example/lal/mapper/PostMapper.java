package com.example.lal.mapper;

import com.example.lal.model.domain.Post;
import com.example.lal.model.entity.PostDetail;
import com.example.lal.model.entity.PostSummary;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Mapper
public interface PostMapper {
    @Insert("INSERT INTO Post (title, userId, content, postTime,  imageurls, authority, updateTime) " +
            "VALUES (#{title}, #{userId}, #{content}, #{postTime},  #{imageurls}, #{authority}, #{updateTime});")
    boolean createPost(PostDetail post);

    @Delete("DELETE FROM Post WHERE id=#{postId} AND (userId=#{userId} OR (#{userId} > 0 AND #{userId} <= 10000000));")
    boolean deletePost(int postId, int userId);

    @Select("SELECT * FROM Post WHERE id=#{postId};")
    PostDetail readPost(int postId);

    @Select("SELECT *, "+
            "CASE WHEN userId = #{userId} "+
            "THEN true ELSE false END "+
            "AS canDelete FROM Post "+
            "ORDER BY id DESC LIMIT #{filter}, #{cntInPage};")
    List<PostSummary> readPostList(int userId, int filter, int cntInPage);

    @Select("SELECT *, "+
            "CASE WHEN userId = #{userId} "+
            "THEN true ELSE false END "+
            "AS canDelete FROM Post "+
            "ORDER BY updateTime DESC LIMIT #{filter}, #{cntInPage};")
    List<PostSummary> readPostListByChange(int userId, int filter, int cntInPage);

    @Select("SELECT *, "+
            "CASE WHEN userId = #{userId} "+
            "THEN true ELSE false END "+
            "AS canDelete FROM Post "+
            "WHERE userId = #{userId} "+
            "ORDER BY updateTime DESC LIMIT #{filter}, #{cntInPage};")
    List<PostSummary> readPostListByMe(int userId, int filter, int cntInPage);

    @Select("SELECT *, "+
            "CASE WHEN userId = #{userId} "+
            "THEN true ELSE false END "+
            "AS canDelete FROM Post "+
            "WHERE userId = #{userId} "+
            "ORDER BY postTime DESC LIMIT #{filter}, #{cntInPage};")
    List<PostSummary> readPostListByOther(int userId, int filter, int cntInPage);

    @Select("SELECT COUNT(*) FROM Post WHERE userId=#{userId};")
    int sumOfUserPosts(int userId);

    @Select("SELECT COUNT(*) FROM Post;")
    int getPostCount();

    @Update("UPDATE Post SET likeCount=likeCount+#{plus} WHERE id=#{postId};")
    boolean updateLike(int postId, int plus);

    @Select("SELECT floorCount FROM Post WHERE id=#{postId};")
    int getFloor(int postId);

    @Update("UPDATE Post SET floorCount=floorCount+#{plus} WHERE id=#{postId};")
    boolean updateFloor(int postId, int plus);


    @Update("UPDATE Post SET browseCount=browseCount+1 WHERE id=#{postId};")
    void addBrowse(int postId);

    @Select("SELECT * "+
            "FROM Post "+
            "ORDER BY (likeCount*10+browseCount) DESC LIMIT 12;")
    List<PostSummary> readPostListByHot(int userId);

    @Update("UPDATE Post SET updateTime=#{time} WHERE id=#{postId};")
    boolean updateUpdateTime(int postId, Date time);

    @Select("SELECT COUNT(*) FROM Post WHERE postTime BETWEEN #{startTime} AND #{endTime};")
    int getPostCountByTime(LocalDateTime startTime, LocalDateTime endTime);

    @Select("SELECT * FROM Post WHERE postTime BETWEEN #{startTime} AND #{endTime};")
    List<Post> getPostByTime(LocalDateTime startTime, LocalDateTime endTime);
}
