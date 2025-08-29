package com.example.lal.service;

import com.example.lal.model.entity.CommentEntry;
import com.example.lal.model.entity.Page;
import com.example.lal.model.exceptions.CommentException;
import com.example.lal.model.request.post.CommentPostRequest;
import com.example.lal.model.request.post.DeleteCommentRequest;
import com.example.lal.model.request.post.ListCommentRequest;

public interface CommentService {
    boolean addComment(CommentPostRequest commentPostRequest, String path) throws CommentException;
    Page<CommentEntry> getCommentList(ListCommentRequest listCommentRequest) throws CommentException;
    boolean deleteComment(DeleteCommentRequest deleteCommentRequest, int curUserId) throws CommentException;
    CommentEntry getComment(int commentId) throws CommentException;
}
