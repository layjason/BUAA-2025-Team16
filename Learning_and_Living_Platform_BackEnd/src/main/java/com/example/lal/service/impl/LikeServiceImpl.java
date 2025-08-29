package com.example.lal.service.impl;

import com.example.lal.mapper.LikeMapper;
import com.example.lal.mapper.PostMapper;
import com.example.lal.model.exceptions.LikeException;
import com.example.lal.service.LikeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;

@Service
public class LikeServiceImpl implements LikeService {
    @Resource
    private LikeMapper likeMapper;

    @Resource
    private PostMapper postMapper;

    @Override
    public boolean getLiked(int curUserId, int postId) throws LikeException{
        return likeMapper.isLike(postId, curUserId);
    }

    @Override
    public boolean addLike(int userId, int postId) throws LikeException{
        Date now = Calendar.getInstance().getTime();
        boolean success1 = likeMapper.createLike(postId, userId, now);
        boolean success2 = postMapper.updateLike(postId, 1);
        return success1 & success2;
    }

    @Override
    public boolean deleteLike(int userId, int postId) throws LikeException{
        boolean success1 = likeMapper.deleteLike(postId, userId);
        boolean success2 = postMapper.updateLike(postId, -1);
        return success1 & success2;
    }

}
