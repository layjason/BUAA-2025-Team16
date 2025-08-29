package com.example.lal.service.impl;

import com.example.lal.constant.UserConstants;
import com.example.lal.mapper.CommentMapper;
import com.example.lal.mapper.ExperienceMapper;
import com.example.lal.mapper.PostMapper;
import com.example.lal.mapper.UserMapper;
import com.example.lal.model.entity.CommentEntry;
import com.example.lal.model.entity.Page;
import com.example.lal.model.exceptions.CommentException;
import com.example.lal.model.request.post.CommentPostRequest;
import com.example.lal.model.request.post.DeleteCommentRequest;
import com.example.lal.model.request.post.ListCommentRequest;
import com.example.lal.service.CommentService;
import com.example.lal.service.ExperienceService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class CommentServiceImpl implements CommentService {

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private PostMapper postMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private ExperienceService experienceService;


    @Override
    public boolean addComment(CommentPostRequest commentPostRequest, String path) throws CommentException {
        CommentEntry commentEntry = new CommentEntry();
        commentEntry.setContent(commentPostRequest.getContent());
        commentEntry.setUserId(commentPostRequest.getUserId());
        commentEntry.setPublishTime(Calendar.getInstance().getTime());
        commentEntry.setPostId(commentPostRequest.getPostId());
        commentEntry.setImageUrl(path);
        postMapper.updateFloor(commentPostRequest.getPostId(), 1);
        commentEntry.setFloor(postMapper.getFloor(commentPostRequest.getPostId()));
        return commentMapper.createComment(commentEntry);
    }

    @Override
    public Page<CommentEntry> getCommentList(ListCommentRequest listCommentRequest) throws CommentException{
        Page<CommentEntry> commentEntryPage = new Page<>();
        int currentPage = listCommentRequest.getPageNum();
        int pageSize = listCommentRequest.getCntInPage();
        int count;
        System.out.println(listCommentRequest.getPostId());
        count = commentMapper.getCommentCount(listCommentRequest.getPostId());

        commentEntryPage.setCount(count);
        commentEntryPage.setCurrPage(currentPage);
        commentEntryPage.setPageSize(pageSize);
        commentEntryPage.setFilterCount((commentEntryPage.getCurrPage() - 1)*commentEntryPage.getPageSize());
        commentEntryPage.setPageCount((commentEntryPage.getCount()+commentEntryPage.getPageSize()-1)/commentEntryPage.getPageSize());
        commentEntryPage.setNextPage(commentEntryPage.getCurrPage()==commentEntryPage.getPageCount()?commentEntryPage.getCurrPage():commentEntryPage.getCurrPage()+1);
        commentEntryPage.setPrePage(commentEntryPage.getCurrPage()==1?commentEntryPage.getCurrPage():commentEntryPage.getCurrPage()-1);
        commentEntryPage.setList(commentMapper.readPostComment(listCommentRequest.getPostId(),listCommentRequest.getUserId(), commentEntryPage.getFilterCount(), commentEntryPage.getPageSize()));
//        List<PostSummary> posts = postMapper.readPostList(0,10);
        for(CommentEntry commentEntry: commentEntryPage.getList()){
            Integer nowId = commentEntry.getUserId();
            if(nowId == null){
                commentEntry.setUserId(UserConstants.DELETE_USER);
                commentEntry.setProfilePhotoUrl(UserConstants.DELETE_AVATAR);
                commentEntry.setUserName(UserConstants.DELETE_NAME);
                commentEntry.setUserLevel(UserConstants.DELETE_AUTHORITY);
                commentEntry.setUserLevelName(null);
            } else {
                commentEntry.setUserName(userMapper.getNameById(nowId));
                //这行代码使用了用户ID查评论
                //commentEntry.setImageUrl(commentMapper.getCommentById(nowId).getImageUrl());
                commentEntry.setProfilePhotoUrl(userMapper.getProfilePhotoUrl(nowId));
                commentEntry.setUserLevel(experienceService.getLevel(nowId));
                int level = experienceService.getLevel(nowId);
                commentEntry.setUserLevel(level);
                commentEntry.setUserLevelName(experienceService.getLevelName(level));
            }

            if(listCommentRequest.getUserId() > 0 && listCommentRequest.getUserId() <= 10000000){
                commentEntry.setCanDelete(true);
            }
//            if(commentEntry.getImageUrl() != null && commentEntry.getImageUrl() != ""){
//                commentEntry.setImageUrlList(Arrays.asList(commentEntry.getImageUrl().split(",")));
//            }
        }
        return commentEntryPage;
    }

    @Override
    public boolean deleteComment(DeleteCommentRequest deleteCommentRequest, int curUserId) throws CommentException{
        return commentMapper.deleteComment(deleteCommentRequest.getCommentId(), curUserId);
    }

    @Override
    public CommentEntry getComment(int commentId) throws CommentException{
        return commentMapper.getCommentById(commentId);
    }
}
