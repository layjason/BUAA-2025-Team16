package com.example.lal.mapper;

import com.example.lal.model.domain.Comment;
import com.example.lal.model.entity.CommentEntry;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Insert("INSERT INTO Comment(postId, userId, publishTime, content, imageUrl, floor) "+
    "VALUES (#{postId}, #{userId}, #{publishTime}, #{content}, #{imageUrl}, #{floor});")
    boolean createComment(CommentEntry comment);

    @Delete("DELETE FROM Comment WHERE id=#{commentId} AND (userId=#{userId} OR (#{userId} > 0 AND #{userId} <= 10000000));")
    boolean deleteComment(int commentId, int userId);

    @Select("SELECT *, "+
            "CASE WHEN userId = #{userId} "+
            "THEN true ELSE false END "+
            "AS canDelete FROM Comment "+
            "WHERE postId=#{postId} "+
            "ORDER BY id ASC LIMIT #{filter}, #{cntInPage};")
    List<CommentEntry> readPostComment(int postId, int userId, int filter, int cntInPage);

    @Select("SELECT * FROM Comment WHERE id=#{commentId};")
    CommentEntry getCommentById(int commentId);

    @Select("SELECT COUNT(*) FROM Comment WHERE postId=#{postId};")
    int getCommentCount(int postId);
}
