package com.example.lal.service.impl;


import com.example.lal.mapper.PostMapper;
import com.example.lal.model.domain.Post;
import com.example.lal.model.entity.Page;
import com.example.lal.model.entity.PostDetail;
import com.example.lal.model.entity.PostSummary;
import com.example.lal.model.exceptions.PostException;

import com.example.lal.model.request.post.AddPostRequest;
import com.example.lal.model.request.post.DeletePostRequest;
import com.example.lal.model.request.post.ListPostRequest;
import com.example.lal.model.request.post.ListUserPostRequest;
import com.example.lal.service.PostService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


@Service
public class PostServiceImpl implements PostService {
    @Resource
    private PostMapper postMapper;

    @Override
    public int addPost(AddPostRequest addPostRequest, String filepath, int userId) throws PostException{
        PostDetail postDetail = new PostDetail();
        postDetail.setTitle(addPostRequest.getTitle());
//        postDetail.setPostTime(addPostRequest.getPostTime());

        postDetail.setAuthority(addPostRequest.getAuthority());
        postDetail.setContent(addPostRequest.getContent());
        postDetail.setImageurls(filepath);
        postDetail.setUserId(userId);
        postDetail.setPostTime(Calendar.getInstance().getTime());
        postDetail.setUpdateTime(Calendar.getInstance().getTime());
        if(postMapper.createPost(postDetail)){

            return postDetail.getId();
        }
        return -1;
    }
    @Override
    public Page<PostSummary> getPostList(int userId, ListPostRequest listPostRequest) throws PostException {
        Page<PostSummary> postSummaryPage = new Page<>();
        int currentPage = listPostRequest.getPageNum();
        int pageSize = listPostRequest.getCntInPage();
        int count;
        count = postMapper.getPostCount();
        postSummaryPage.setCount(count);
        postSummaryPage.setCurrPage(currentPage);
        postSummaryPage.setPageSize(pageSize);
        postSummaryPage.setFilterCount((postSummaryPage.getCurrPage() - 1)*postSummaryPage.getPageSize());
        postSummaryPage.setPageCount((postSummaryPage.getCount()+postSummaryPage.getPageSize()-1)/postSummaryPage.getPageSize());
        postSummaryPage.setNextPage(postSummaryPage.getCurrPage()==postSummaryPage.getPageCount()?postSummaryPage.getCurrPage():postSummaryPage.getCurrPage()+1);
        postSummaryPage.setPrePage(postSummaryPage.getCurrPage()==1?postSummaryPage.getCurrPage():postSummaryPage.getCurrPage()-1);

        if(1 == listPostRequest.getMode()){
            postSummaryPage.setList(postMapper.readPostList(userId, postSummaryPage.getFilterCount(), postSummaryPage.getPageSize()));
        } else if (2 == listPostRequest.getMode()) {
            postSummaryPage.setList(postMapper.readPostListByChange(userId, postSummaryPage.getFilterCount(), postSummaryPage.getPageSize()));
        } else {
            postSummaryPage.setList(postMapper.readPostListByMe(userId, postSummaryPage.getFilterCount(), postSummaryPage.getPageSize()));
            postSummaryPage.setCount(postMapper.sumOfUserPosts(userId));
            postSummaryPage.setPageCount((postSummaryPage.getCount()+postSummaryPage.getPageSize()-1)/postSummaryPage.getPageSize());
            postSummaryPage.setNextPage(postSummaryPage.getCurrPage()==postSummaryPage.getPageCount()?postSummaryPage.getCurrPage():postSummaryPage.getCurrPage()+1);
        }

        for(PostSummary postSummary: postSummaryPage.getList()){
            postSummary.setHotPoint(postSummary.getLikeCount()* 10L +postSummary.getBrowseCount());
        }

//        List<PostSummary> posts = postMapper.readPostList(0,10);
        return postSummaryPage;
    }

    @Override
    public PostDetail getPost(int postId) throws PostException{
        PostDetail postDetail = new PostDetail();
        postDetail = postMapper.readPost(postId);
        if(postDetail == null){
            throw new PostException("Post not found, Id: "+postId);
        }
        else{
            postDetail.setImageUrlList(Arrays.asList(postDetail.getImageurls().split(",")));
        }
        postDetail.setHotPoint(postDetail.getLikeCount()* 10L +postDetail.getBrowseCount());
        return postDetail;
    }

    @Override
    public boolean deletePost(DeletePostRequest deletePostRequest) throws PostException{
        return postMapper.deletePost(deletePostRequest.getPostId(), deletePostRequest.getUserId());
    }

    @Override
    public void addBrowse(int postId){
        postMapper.addBrowse(postId);
    }

    @Override
    public List<PostSummary> getPostByHot(int userId) throws PostException {
        List<PostSummary> postSummaryList = postMapper.readPostListByHot(userId);
        for(PostSummary postSummary: postSummaryList){
            postSummary.setHotPoint(postSummary.getLikeCount()* 10L +postSummary.getBrowseCount());
        }
        return postSummaryList;
    }


    @Override
    public boolean updateUpdateTime(int postId) throws PostException{
        Date date = Calendar.getInstance().getTime();
        return postMapper.updateUpdateTime(postId, date);
    }
    @Override
    public int getPostCountByTime(LocalDateTime startTime, LocalDateTime endTime){
        return postMapper.getPostCountByTime(startTime, endTime);
    }
    @Override
    public List<Post> getPostByTime(LocalDateTime startTime, LocalDateTime endTime){
        return postMapper.getPostByTime(startTime, endTime);
    }

    @Override
    public int getAllPostCount(){
        return postMapper.getPostCount();
    }

    @Override
    public Page<PostSummary> postsOfUser(ListUserPostRequest listUserPostRequest) throws PostException{
        Page<PostSummary> postSummaryPage = new Page<>();
        int currentPage = listUserPostRequest.getPageNum();
        int pageSize = listUserPostRequest.getCntInPage();
        int count;
        postSummaryPage.setCurrPage(currentPage);
        postSummaryPage.setPageSize(pageSize);
        postSummaryPage.setCount(postMapper.sumOfUserPosts(listUserPostRequest.getUserId()));
        postSummaryPage.setFilterCount((postSummaryPage.getCurrPage() - 1)*postSummaryPage.getPageSize());
        postSummaryPage.setPrePage(postSummaryPage.getCurrPage()==1?postSummaryPage.getCurrPage():postSummaryPage.getCurrPage()-1);
        postSummaryPage.setPageCount((postSummaryPage.getCount()+postSummaryPage.getPageSize()-1)/postSummaryPage.getPageSize());
        postSummaryPage.setNextPage(postSummaryPage.getCurrPage()==postSummaryPage.getPageCount()?postSummaryPage.getCurrPage():postSummaryPage.getCurrPage()+1);
        postSummaryPage.setList(postMapper.readPostListByOther(listUserPostRequest.getUserId(), postSummaryPage.getFilterCount(), postSummaryPage.getPageSize()));

        for(PostSummary postSummary: postSummaryPage.getList()){
            postSummary.setHotPoint(postSummary.getLikeCount()* 10L +postSummary.getBrowseCount());
        }

        return postSummaryPage;
    }
}
