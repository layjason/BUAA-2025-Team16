package com.example.lal.service.impl;

import com.example.lal.constant.UserConstants;
import com.example.lal.mapper.ReplyMapper;
import com.example.lal.mapper.UserMapper;
import com.example.lal.model.entity.ReplyEntry;
import com.example.lal.model.exceptions.ReplyException;
import com.example.lal.model.request.post.DeleteReplyRequest;
import com.example.lal.model.request.post.ListReplyRequest;
import com.example.lal.model.request.post.ReplyCommentRequest;
import com.example.lal.service.ExperienceService;
import com.example.lal.service.ReplyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;

@Service
public class ReplyServiceImpl implements ReplyService {

    @Resource
    private ReplyMapper replyMapper;

    @Resource
    private ExperienceService experienceService;

    @Resource
    private UserMapper userMapper;
    @Override
    public List<ReplyEntry> getReplyList(ListReplyRequest listReplyRequest) throws ReplyException {
        //System.out.println(listReplyRequest.getCommentId());
        int count = listReplyRequest.getCntInPage();
        List<ReplyEntry> replyEntryList = replyMapper.readCommendReply(listReplyRequest.getCommentId(), listReplyRequest.getUserId(), count);

        for(ReplyEntry replyEntry: replyEntryList){
            Integer nowId = replyEntry.getUserId();
            if(nowId == null){
                replyEntry.setUserId(UserConstants.DELETE_USER);
                replyEntry.setProfilePhotoUrl(UserConstants.DELETE_AVATAR);
                replyEntry.setUserName(UserConstants.DELETE_NAME);
                replyEntry.setUserLevel(UserConstants.DELETE_AUTHORITY);
                replyEntry.setUserLevelName(null);
            } else {
                replyEntry.setUserName(userMapper.getNameById(nowId));
                replyEntry.setProfilePhotoUrl(userMapper.getProfilePhotoUrl(nowId));
                int level = experienceService.getLevel(nowId);
                replyEntry.setUserLevel(level);
                replyEntry.setUserLevelName(experienceService.getLevelName(level));
            }

            if(listReplyRequest.getUserId() > 0 && listReplyRequest.getUserId() <= 10000000){
                replyEntry.setCanDelete(true);
            }
        }
        return replyEntryList;
    }

    @Override
    public ReplyEntry addReply(ReplyCommentRequest replyCommentRequest) throws ReplyException{
        ReplyEntry replyEntry = new ReplyEntry();
        replyEntry.setUserId(replyCommentRequest.getUserId());
        replyEntry.setPublishTime(Calendar.getInstance().getTime());
        replyEntry.setContent(replyCommentRequest.getContent());
        replyEntry.setUserName(userMapper.getNameById(replyCommentRequest.getUserId()));
        replyEntry.setProfilePhotoUrl(userMapper.getProfilePhotoUrl(replyCommentRequest.getUserId()));
        replyEntry.setCanDelete(true);
        replyEntry.setPostId(replyCommentRequest.getPostId());
        replyEntry.setCommentId(replyCommentRequest.getCommentId());
        if(replyMapper.createReply(replyEntry)){
            return replyEntry;
        } else {
            return null;
        }
    }

    @Override
    public boolean deleteReply(DeleteReplyRequest deleteReplyRequest, int userId) throws ReplyException{
        return replyMapper.deleteReply(deleteReplyRequest.getReplyId(), userId);
    }
}
