package com.example.lal.service;

import com.example.lal.model.exceptions.LikeException;

public interface LikeService {
    public boolean getLiked(int curUserId, int postId) throws LikeException;
    public boolean addLike(int userId, int postId) throws LikeException;
    public boolean deleteLike(int userId, int postId) throws LikeException;

}
