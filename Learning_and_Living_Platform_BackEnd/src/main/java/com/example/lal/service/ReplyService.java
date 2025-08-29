package com.example.lal.service;

import com.example.lal.model.entity.ReplyEntry;
import com.example.lal.model.exceptions.ReplyException;
import com.example.lal.model.request.post.DeleteReplyRequest;
import com.example.lal.model.request.post.ListReplyRequest;
import com.example.lal.model.request.post.ReplyCommentRequest;

import java.util.List;

public interface ReplyService {

    ReplyEntry addReply(ReplyCommentRequest replyCommentRequest) throws ReplyException;
    List<ReplyEntry> getReplyList(ListReplyRequest listReplyRequest) throws ReplyException;

    boolean deleteReply(DeleteReplyRequest deleteReplyRequest, int userId) throws ReplyException;
}
