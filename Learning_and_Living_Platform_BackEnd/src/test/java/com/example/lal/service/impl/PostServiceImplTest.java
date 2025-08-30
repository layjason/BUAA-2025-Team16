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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    @Test
    void addPost() throws PostException {
        // Positive case: Successful post creation
        AddPostRequest request = new AddPostRequest();
        request.setTitle("Test Title");
        request.setContent("Test Content");
        request.setAuthority(1);
        String filepath = "image1.jpg,image2.jpg";
        int userId = 10000001;  // Valid user ID

        // Mock the insert to succeed and simulate ID setting
        when(postMapper.createPost(any(PostDetail.class))).thenAnswer(invocation -> {
            PostDetail postDetail = invocation.getArgument(0);
            postDetail.setId(1);  // Simulate auto-generated ID
            return true;
        });

        int result = postService.addPost(request, filepath, userId);
        assertEquals(1, result);
        verify(postMapper).createPost(argThat(post ->
                post.getTitle().equals("Test Title") &&
                        post.getContent().equals("Test Content") &&
                        post.getImageurls().equals(filepath) &&
                        post.getUserId() == userId &&
                        post.getPostTime() != null &&
                        post.getUpdateTime() != null
        ));

        // Negative case: Mapper insert fails
        when(postMapper.createPost(any(PostDetail.class))).thenReturn(false);

        int failedResult = postService.addPost(request, filepath, userId);
        assertEquals(-1, failedResult);
        verify(postMapper, times(2)).createPost(any(PostDetail.class));  // Cumulative calls
    }

    @Test
    void getPostList() throws PostException {
        // Positive case: Mode 1, with posts and pagination
        ListPostRequest request = new ListPostRequest();
        request.setMode(1);
        request.setPageNum(1);
        request.setCntInPage(10);
        int userId = 10000001;

        when(postMapper.getPostCount()).thenReturn(20);
        PostSummary summary = new PostSummary();
        summary.setLikeCount(5);
        summary.setBrowseCount(10);  // hotPoint = 5*10 + 10 = 60
        List<PostSummary> mockList = Arrays.asList(summary);
        when(postMapper.readPostList(userId, 0, 10)).thenReturn(mockList);

        Page<PostSummary> result = postService.getPostList(userId, request);
        assertEquals(20, result.getCount());
        assertEquals(2, result.getPageCount());
        assertEquals(1, result.getList().size());
        assertEquals(60L, result.getList().get(0).getHotPoint());
        verify(postMapper).getPostCount();
        verify(postMapper).readPostList(userId, 0, 10);

        // Negative case: No posts (empty list, count 0)
        when(postMapper.getPostCount()).thenReturn(0);
        when(postMapper.readPostList(userId, 0, 10)).thenReturn(Arrays.asList());

        Page<PostSummary> emptyResult = postService.getPostList(userId, request);
        assertEquals(0, emptyResult.getCount());
        assertEquals(0, emptyResult.getPageCount());
        assertTrue(emptyResult.getList().isEmpty());
        verify(postMapper, times(2)).getPostCount();  // Cumulative
    }

    @Test
    void getPost() throws PostException {
        // Positive case: Post exists with images
        int postId = 1;
        PostDetail mockDetail = new PostDetail();
        mockDetail.setId(postId);
        mockDetail.setImageurls("img1.jpg,img2.jpg");
        mockDetail.setLikeCount(3);
        mockDetail.setBrowseCount(4);  // hotPoint = 3*10 + 4 = 34
        when(postMapper.readPost(postId)).thenReturn(mockDetail);

        PostDetail result = postService.getPost(postId);
        assertEquals(postId, result.getId());
        assertEquals(Arrays.asList("img1.jpg", "img2.jpg"), result.getImageUrlList());
        assertEquals(34L, result.getHotPoint());
        verify(postMapper).readPost(postId);

        // Negative case: Post does not exist (null from mapper)
        when(postMapper.readPost(postId)).thenReturn(null);

        PostException exception = assertThrows(PostException.class, () -> postService.getPost(postId));
        assertTrue(exception.getMessage().contains("Post not found, Id: " + postId));  // Verify message
        verify(postMapper, times(2)).readPost(postId);
    }

    @Test
    void deletePost() throws PostException {
        // Positive case: Successful deletion
        DeletePostRequest request = new DeletePostRequest();
        request.setPostId(1);
        request.setUserId(10000001);
        when(postMapper.deletePost(1, 10000001)).thenReturn(true);

        boolean result = postService.deletePost(request);
        assertTrue(result);
        verify(postMapper).deletePost(1, 10000001);

        // Negative case: Deletion fails (e.g., not owner or admin)
        when(postMapper.deletePost(1, 10000001)).thenReturn(false);

        boolean failedResult = postService.deletePost(request);
        assertFalse(failedResult);
        verify(postMapper, times(2)).deletePost(1, 10000001);
    }

    @Test
    void addBrowse() {
        // Positive case: Successful browse increment (void method, verify call)
        int postId = 1;
        postService.addBrowse(postId);
        verify(postMapper).addBrowse(postId);

        // Negative case: No real negative for void, but simulate (e.g., verify not called if invalid, but method always calls)
        // For coverage, call again and verify multiple
        postService.addBrowse(postId);
        verify(postMapper, times(2)).addBrowse(postId);
    }

    @Test
    void getPostByHot() throws PostException {
        // Positive case: Hot posts returned with hotPoint
        int userId = 10000001;
        PostSummary summary = new PostSummary();
        summary.setLikeCount(10);
        summary.setBrowseCount(20);  // hotPoint = 10*10 + 20 = 120
        List<PostSummary> mockList = Arrays.asList(summary);
        when(postMapper.readPostListByHot(userId)).thenReturn(mockList);

        List<PostSummary> result = postService.getPostByHot(userId);
        assertEquals(1, result.size());
        assertEquals(120L, result.get(0).getHotPoint());
        verify(postMapper).readPostListByHot(userId);

        // Negative case: No hot posts (empty list)
        when(postMapper.readPostListByHot(userId)).thenReturn(Arrays.asList());

        List<PostSummary> emptyResult = postService.getPostByHot(userId);
        assertTrue(emptyResult.isEmpty());
        verify(postMapper, times(2)).readPostListByHot(userId);
    }

    @Test
    void updateUpdateTime() throws PostException {
        // Positive case: Successful update
        int postId = 1;
        when(postMapper.updateUpdateTime(eq(postId), any(Date.class))).thenReturn(true);

        boolean result = postService.updateUpdateTime(postId);
        assertTrue(result);
        verify(postMapper).updateUpdateTime(eq(postId), any(Date.class));

        // Negative case: Update fails
        when(postMapper.updateUpdateTime(eq(postId), any(Date.class))).thenReturn(false);

        boolean failedResult = postService.updateUpdateTime(postId);
        assertFalse(failedResult);
        verify(postMapper, times(2)).updateUpdateTime(eq(postId), any(Date.class));
    }

    @Test
    void getPostCountByTime() {
        // Positive case: Posts in time range
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        when(postMapper.getPostCountByTime(start, end)).thenReturn(15);

        int result = postService.getPostCountByTime(start, end);
        assertEquals(15, result);
        verify(postMapper).getPostCountByTime(start, end);

        // Negative case: No posts (0 count)
        when(postMapper.getPostCountByTime(start, end)).thenReturn(0);

        int zeroResult = postService.getPostCountByTime(start, end);
        assertEquals(0, zeroResult);
        verify(postMapper, times(2)).getPostCountByTime(start, end);
    }

    @Test
    void getPostByTime() {
        // Positive case: Posts in time range
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        List<Post> mockPosts = Arrays.asList(new Post(), new Post());
        when(postMapper.getPostByTime(start, end)).thenReturn(mockPosts);

        List<Post> result = postService.getPostByTime(start, end);
        assertEquals(2, result.size());
        verify(postMapper).getPostByTime(start, end);

        // Negative case: No posts
        when(postMapper.getPostByTime(start, end)).thenReturn(Arrays.asList());

        List<Post> emptyResult = postService.getPostByTime(start, end);
        assertTrue(emptyResult.isEmpty());
        verify(postMapper, times(2)).getPostByTime(start, end);
    }

    @Test
    void getAllPostCount() {
        // Positive case: Non-zero count
        when(postMapper.getPostCount()).thenReturn(100);

        int result = postService.getAllPostCount();
        assertEquals(100, result);
        verify(postMapper).getPostCount();

        // Negative case: Zero count
        when(postMapper.getPostCount()).thenReturn(0);

        int zeroResult = postService.getAllPostCount();
        assertEquals(0, zeroResult);
        verify(postMapper, times(2)).getPostCount();
    }

    @Test
    void postsOfUser() throws PostException {
        // Positive case: User has posts with pagination
        ListUserPostRequest request = new ListUserPostRequest();
        request.setUserId(10000001);
        request.setPageNum(1);
        request.setCntInPage(10);

        when(postMapper.sumOfUserPosts(10000001)).thenReturn(15);
        PostSummary summary = new PostSummary();
        summary.setLikeCount(1);
        summary.setBrowseCount(2);  // hotPoint = 1*10 + 2 = 12
        List<PostSummary> mockList = Arrays.asList(summary);
        when(postMapper.readPostListByOther(10000001, 0, 10)).thenReturn(mockList);

        Page<PostSummary> result = postService.postsOfUser(request);
        assertEquals(15, result.getCount());
        assertEquals(2, result.getPageCount());
        assertEquals(1, result.getList().size());
        assertEquals(12L, result.getList().get(0).getHotPoint());
        verify(postMapper).sumOfUserPosts(10000001);
        verify(postMapper).readPostListByOther(10000001, 0, 10);

        // Negative case: User has no posts
        when(postMapper.sumOfUserPosts(10000001)).thenReturn(0);
        when(postMapper.readPostListByOther(10000001, 0, 10)).thenReturn(Arrays.asList());

        Page<PostSummary> emptyResult = postService.postsOfUser(request);
        assertEquals(0, emptyResult.getCount());
        assertEquals(0, emptyResult.getPageCount());
        assertTrue(emptyResult.getList().isEmpty());
        verify(postMapper, times(2)).sumOfUserPosts(10000001);
    }
}