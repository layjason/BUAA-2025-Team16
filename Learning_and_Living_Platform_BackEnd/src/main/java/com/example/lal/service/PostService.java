package com.example.lal.service;

import com.example.lal.model.domain.Post;
import com.example.lal.model.entity.Page;
import com.example.lal.model.entity.PostDetail;
import com.example.lal.model.entity.PostSummary;
import com.example.lal.model.exceptions.PostException;
import com.example.lal.model.request.post.AddPostRequest;
import com.example.lal.model.request.post.DeletePostRequest;
import com.example.lal.model.request.post.ListPostRequest;
import com.example.lal.model.request.post.ListUserPostRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {
    Page<PostSummary> getPostList(int userId, ListPostRequest listPostRequest) throws PostException;
    int addPost(AddPostRequest addPostRequest, String filepath, int userId) throws PostException;
    PostDetail getPost(int postId) throws PostException;
    boolean deletePost(DeletePostRequest deletePostRequest) throws PostException;
    void addBrowse(int postId) throws PostException;
    List<PostSummary> getPostByHot(int userId) throws PostException;
    boolean updateUpdateTime(int postId) throws PostException;

    int getPostCountByTime(LocalDateTime startTime, LocalDateTime endTime);

    List<Post> getPostByTime(LocalDateTime startTime, LocalDateTime endTime);

    int getAllPostCount();

    Page<PostSummary> postsOfUser(ListUserPostRequest listUserPostRequest)throws PostException;
}
