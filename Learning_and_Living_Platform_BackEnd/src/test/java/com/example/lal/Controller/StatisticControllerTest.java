package com.example.lal.Controller;

import com.example.lal.model.RestBean;
import com.example.lal.model.domain.Post;
import com.example.lal.model.entity.*;
import com.example.lal.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date; // 添加导入
import java.util.List;

import static com.example.lal.constant.OtherConstants.CATEGORY_NUM;
import static com.example.lal.constant.OtherConstants.SUBJECT_NUM;
import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
        when(mockRequest.getAttribute("userId")).thenReturn("1");
    }

    // 测试权限检查
    @Test
    void testRestrictAccess_Unauthorized() {
        when(mockRequest.getAttribute("userId")).thenReturn("10000001");
        RestBean response = statisticController.getOverallFigure(mockRequest).getBody();
        assertFalse(response.isSuccess());
        assertEquals("你没有权限查看统计信息", response.getMessage());
    }

    @Test
    void testRestrictAccess_InvalidUserIdFormat() {
        when(mockRequest.getAttribute("userId")).thenReturn("invalid");
        RestBean response = statisticController.getOverallFigure(mockRequest).getBody();
        assertFalse(response.isSuccess());
        assertEquals("无效的用户ID格式", response.getMessage());
    }

    // getOverallFigure 测试
    @Test
    void testGetOverallFigure_Success() {
        when(userService.getAllUserOnlineCount()).thenReturn(10);
        when(userService.getAllUserCount()).thenReturn(50);
        when(resourceService.getAllResourceCount()).thenReturn(30);
        when(postService.getAllPostCount()).thenReturn(20);

        RestBean response = statisticController.getOverallFigure(mockRequest).getBody();
        assertTrue(response.isSuccess());
        OverallFigure data = (OverallFigure) response.getMessage();
        assertEquals(10, data.getNumOfOnlineUser());
        assertEquals(50, data.getNumOfUser());
        assertEquals(30, data.getNumOfResource());
        assertEquals(20, data.getNumOfPost());
    }

    @Test
    void testGetOverallFigure_ServiceThrowsException() {
        when(userService.getAllUserOnlineCount()).thenThrow(new RuntimeException("服务错误"));
        assertThrows(RuntimeException.class, () -> statisticController.getOverallFigure(mockRequest));
    }

    // resourceClassificationStatistics 测试
    @Test
    void testResourceClassificationStatistics_Success() {
        for (int i = 0; i < CATEGORY_NUM; i++) {
            for (int j = 0; j < SUBJECT_NUM; j++) {
                when(resourceService.getResourceNumByCategoryAndSubject(i, j)).thenReturn(i + j);
            }
        }
        RestBean response = statisticController.resourceClassificationStatistics(mockRequest).getBody();
        assertTrue(response.isSuccess());
        int[][] result = (int[][]) response.getMessage();
        assertEquals(CATEGORY_NUM, result.length);
        assertEquals(SUBJECT_NUM, result[0].length);
        assertEquals(2, result[1][1]); // 验证具体值
    }

    // resourceDownloadsByDays 测试
    @Test
    void testResourceDownloadsByDays_Success() throws Exception {
        when(downloadHistoryService.getDownloadsByTime(any(), any())).thenReturn(5);
        when(resourceService.getResourceCountByTime(any(), any())).thenReturn(3);

        String output = tapSystemOut(() -> {
            RestBean response = statisticController.resourceDownloadsByDays(mockRequest).getBody();
            assertTrue(response.isSuccess());
            ResourceUploadsAndDownloadsLineChart data = (ResourceUploadsAndDownloadsLineChart) response.getMessage();
            assertEquals(30, data.getDays().length);
            assertEquals(30, data.getDownloads().length);
            assertEquals(30, data.getUploads().length);
            assertEquals(5, data.getDownloads()[0]);
            assertEquals(3, data.getUploads()[0]);
        });
        assertFalse(output.contains("过去一月的资源下载量是")); // 确保旧的错误日志不再出现
    }

    @Test
    void testResourceDownloadsByDays_EmptyResults() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.minusDays(29);
        when(downloadHistoryService.getDownloadsByTime(any(), any())).thenReturn(0);
        when(resourceService.getResourceCountByTime(any(), any())).thenReturn(0);

        RestBean response = statisticController.resourceDownloadsByDays(mockRequest).getBody();
        ResourceUploadsAndDownloadsLineChart data = (ResourceUploadsAndDownloadsLineChart) response.getMessage();
        assertArrayEquals(new int[30], data.getDownloads());
        assertArrayEquals(new int[30], data.getUploads());
    }

    // resourceCountBySubject 测试
    @Test
    void testResourceCountBySubject_Success() {
        for (int i = 0; i < SUBJECT_NUM; i++) {
            when(resourceService.getResourceCountBySubject(i)).thenReturn(i);
        }
        RestBean response = statisticController.resourceCountBySubject(mockRequest).getBody();
        int[] result = (int[]) response.getMessage();
        assertEquals(SUBJECT_NUM, result.length);
        assertEquals(1, result[1]); // 验证具体值
    }

    // userRegisterCountByDays 测试
    @Test
    void testUserRegisterCountByDays_Success() throws Exception {
        when(userService.getUserCountByTime(any(), any())).thenReturn(2);

        String output = tapSystemOut(() -> {
            RestBean response = statisticController.userRegisterCountByDays(mockRequest).getBody();
            UserRegisterCountLineChart data = (UserRegisterCountLineChart) response.getMessage();
            assertEquals(30, data.getDays().length);
            assertEquals(30, data.getCount().length);
            assertEquals(2, data.getCount()[0]);
        });
        assertTrue(output.contains("过去30天的注册用户数量是:60")); // 30 * 2
    }

    // hotPostByDays 测试
    @Test
    void testHotPostByDays_Success() {
        Post mockPost = new Post();
        mockPost.setPostTime(new Timestamp(System.currentTimeMillis()));
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
        assertEquals("Test Post", data.getData()[0][6]);

        // 验证日期序列
        LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime sevenDaysAgo = now.minusDays(6);
        for (int i = 0; i < 7; i++) {
            Date expectedDate = Date.from(sevenDaysAgo.plusDays(i).atZone(ZoneId.systemDefault()).toInstant());
            assertEquals(expectedDate, data.getDate()[i]);
        }
    }
}