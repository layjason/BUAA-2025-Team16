package com.example.lal.Controller;

import com.example.lal.model.RestBean;
import com.example.lal.model.domain.Post;
import com.example.lal.model.entity.*;
import com.example.lal.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import static com.example.lal.constant.OtherConstants.CATEGORY_NUM;
import static com.example.lal.constant.OtherConstants.SUBJECT_NUM;


import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@WebMvcTest(StatisticController.class)
public class StatisticControllerTest {

    @Autowired
    private StatisticController statisticController;

    @MockBean
    private ResourceService resourceService;

    @MockBean
    private DownloadHistoryService downloadHistoryService;

    @MockBean
    private UserService userService;

    @MockBean
    private PostService postService;

    @MockBean
    private ExperienceService experienceService;

    private HttpServletRequest mockRequest;

    @BeforeEach
    void setup() {
        mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getAttribute("userId")).thenReturn("1"); // 有权限
    }

    // ---------- getOverallFigure ----------
    @Test
    void testGetOverallFigure_Positive() {
        when(userService.getAllUserOnlineCount()).thenReturn(10);
        when(userService.getAllUserCount()).thenReturn(50);
        when(resourceService.getAllResourceCount()).thenReturn(30);
        when(postService.getAllPostCount()).thenReturn(20);

        RestBean response = statisticController.getOverallFigure(mockRequest).getBody();
        assertNotNull(response);
        OverallFigure data = (OverallFigure) response.getMessage();
        assertEquals(10, data.getNumOfOnlineUser());
        assertEquals(50, data.getNumOfUser());
        assertEquals(30, data.getNumOfResource());
        assertEquals(20, data.getNumOfPost());
    }

    @Test
    void testGetOverallFigure_Unauthorized() {
        when(mockRequest.getAttribute("userId")).thenReturn("10000001");
        RestBean response = statisticController.getOverallFigure(mockRequest).getBody();
        assertFalse(response.isSuccess());
    }

    // ---------- resourceClassificationStatistics ----------
    @Test
    void testResourceClassificationStatistics_Positive() {
        for (int i = 0; i < CATEGORY_NUM; i++) {
            for (int j = 0; j < SUBJECT_NUM; j++) {
                when(resourceService.getResourceNumByCategoryAndSubject(i, j)).thenReturn(i + j);
            }
        }
        RestBean response = statisticController.resourceClassificationStatistics(mockRequest).getBody();
        assertNotNull(response);
        int[][] result = (int[][]) response.getMessage();
        assertEquals(CATEGORY_NUM, result.length);
        assertEquals(SUBJECT_NUM, result[0].length);
    }

    // ---------- resourceDownloadsByDays ----------
    @Test
    void testResourceDownloadsByDays_Positive() {
        when(downloadHistoryService.getDownloadsByTime(any(), any())).thenReturn(5);
        when(resourceService.getResourceCountByTime(any(), any())).thenReturn(3);

        RestBean response = statisticController.resourceDownloadsByDays(mockRequest).getBody();
        assertNotNull(response);
        ResourceUploadsAndDownloadsLineChart data = (ResourceUploadsAndDownloadsLineChart) response.getMessage();
        assertEquals(30, data.getDays().length);
        assertEquals(30, data.getDownloads().length);
        assertEquals(30, data.getUploads().length);
    }

    // ---------- resourceCountBySubject ----------
    @Test
    void testResourceCountBySubject_Positive() {
        for (int i = 0; i < SUBJECT_NUM; i++) {
            when(resourceService.getResourceCountBySubject(i)).thenReturn(i);
        }
        RestBean response = statisticController.resourceCountBySubject(mockRequest).getBody();
        int[] result = (int[]) response.getMessage();
        assertEquals(SUBJECT_NUM, result.length);
    }

    // ---------- userRegisterCountByDays ----------
    @Test
    void testUserRegisterCountByDays_Positive() {
        when(userService.getUserCountByTime(any(), any())).thenReturn(2);
        RestBean response = statisticController.userRegisterCountByDays(mockRequest).getBody();
        UserRegisterCountLineChart data = (UserRegisterCountLineChart) response.getMessage();
        assertEquals(30, data.getDays().length);
        assertEquals(30, data.getCount().length);
    }

    // ---------- hotPostByDays ----------
    @Test
    void testHotPostByDays_Positive() {
        Post mockPost = new Post();
        mockPost.setPostTime(new Timestamp(System.currentTimeMillis() - 24 * 3600 * 1000L));
        mockPost.setLikeCount(2);
        mockPost.setBrowseCount(10);
        mockPost.setFloorCount(1);
        mockPost.setTitle("Test Post");
        mockPost.setUserId(1);

        when(postService.getPostCountByTime(any(), any())).thenReturn(1);
        when(postService.getPostByTime(any(), any())).thenReturn(List.of(mockPost));
        when(experienceService.getLevel(anyInt())).thenReturn(1);

        RestBean response = statisticController.hotPostByDays(mockRequest).getBody();
        HotPostStatistic data = (HotPostStatistic) response.getMessage();
        assertEquals(7, data.getDate().length);
        assertEquals(1, data.getData().length);
        assertEquals(7, data.getData()[0].length);
    }
}

