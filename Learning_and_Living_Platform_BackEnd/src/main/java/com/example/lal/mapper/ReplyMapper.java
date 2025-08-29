package com.example.lal.mapper;

import com.example.lal.model.domain.Reply;
import com.example.lal.model.entity.ReplyEntry;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ReplyMapper {
    @Insert("INSERT INTO Reply(postId, commentId, userId, publishTime, content) "+
    "VALUES (#{postId}, #{commentId}, #{userId}, #{publishTime}, #{content});")
    boolean createReply(ReplyEntry reply);

    @Delete("DELETE FROM Reply WHERE id=#{replyId} AND (userId=#{userId} OR (#{userId} > 0 AND #{userId} <= 10000000));")
    boolean deleteReply(int replyId, int userId);

    @Select("SELECT *, "+
            "CASE WHEN userId = #{userId} "+
            "THEN true ELSE false END "+
            "AS canDelete FROM Reply "+
            "WHERE commentId=#{commentId} "+
            "ORDER BY id ASC LIMIT #{cntInPage};")
    List<ReplyEntry> readCommendReply(int commentId, int userId, int cntInPage);

}
