package com.example.lal.Controller;

import com.example.lal.model.RestBean;
import com.example.lal.model.domain.Post;
import com.example.lal.model.entity.*;
import com.example.lal.service.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static com.example.lal.constant.OtherConstants.CATEGORY_NUM;
import static com.example.lal.constant.OtherConstants.SUBJECT_NUM;

@RestController
@RequestMapping("/statistic")
public class StatisticController {
    @Resource
    private ResourceService resourceService;
    @Resource
    private DownloadHistoryService downloadHistoryService;
    @Resource
    private UserService userService;
    @Resource
    private PostService postService;
    @Resource
    private ExperienceService experienceService;

    private ResponseEntity<RestBean> restrictAccess(String userId) {
        try {
            int id = Integer.parseInt(userId);
            if (id > 10000000 || id <= 0) {
                System.out.println("你没有权限查看统计信息");
                return RestBean.failure(HttpStatus.UNAUTHORIZED, "你没有权限查看统计信息");
            }
            return null;
        } catch (NumberFormatException e) {
            System.out.println("无效的用户ID格式");
            return RestBean.failure(HttpStatus.BAD_REQUEST, "无效的用户ID格式");
        }
    }

    @GetMapping("/getOverallFigure")
    public ResponseEntity<RestBean> getOverallFigure(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        ResponseEntity<RestBean> accessCheck = restrictAccess(userId);
        if (accessCheck != null) return accessCheck;

        OverallFigure overallFigure = new OverallFigure();
        overallFigure.setNumOfOnlineUser(userService.getAllUserOnlineCount());
        overallFigure.setNumOfUser(userService.getAllUserCount());
        overallFigure.setNumOfResource(resourceService.getAllResourceCount());
        overallFigure.setNumOfPost(postService.getAllPostCount());
        return RestBean.success(overallFigure);
    }

    @GetMapping("/resourceClassificationStatistics")
    public ResponseEntity<RestBean> resourceClassificationStatistics(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        ResponseEntity<RestBean> accessCheck = restrictAccess(userId);
        if (accessCheck != null) return accessCheck;

        int[][] resourceNum = new int[CATEGORY_NUM][SUBJECT_NUM];
        for (int i = 0; i < CATEGORY_NUM; i++) {
            for (int j = 0; j < SUBJECT_NUM; j++) {
                resourceNum[i][j] = resourceService.getResourceNumByCategoryAndSubject(i, j);
            }
        }
        return RestBean.success(resourceNum);
    }

    @GetMapping("/resDownsAndUpsByDays")
    public ResponseEntity<RestBean> resourceDownloadsByDays(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        ResponseEntity<RestBean> accessCheck = restrictAccess(userId);
        if (accessCheck != null) return accessCheck;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDay = now.minusDays(29).withHour(0).withMinute(0).withSecond(0).withNano(0);

        ResourceUploadsAndDownloadsLineChart chart = new ResourceUploadsAndDownloadsLineChart();
        chart.setDownloads(new int[30]);
        chart.setUploads(new int[30]);
        chart.setDays(new Date[30]);

        for (int i = 0; i < 30; i++) {
            LocalDateTime currentDay = startDay.plusDays(i);
            LocalDateTime nextDay = currentDay.plusDays(1);
            chart.getDays()[i] = Date.from(currentDay.atZone(ZoneId.systemDefault()).toInstant());
            chart.getDownloads()[i] = downloadHistoryService.getDownloadsByTime(currentDay, nextDay.isAfter(now) ? now : nextDay);
            chart.getUploads()[i] = resourceService.getResourceCountByTime(currentDay, nextDay.isAfter(now) ? now : nextDay);
        }

        return RestBean.success(chart);
    }

    @GetMapping("/resourceCountBySubject")
    public ResponseEntity<RestBean> resourceCountBySubject(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        ResponseEntity<RestBean> accessCheck = restrictAccess(userId);
        if (accessCheck != null) return accessCheck;

        int[] resourceNum = new int[SUBJECT_NUM];
        for (int i = 0; i < SUBJECT_NUM; i++) {
            resourceNum[i] = resourceService.getResourceCountBySubject(i);
        }
        return RestBean.success(resourceNum);
    }

    @GetMapping("/userRegisterCountByDays")
    public ResponseEntity<RestBean> userRegisterCountByDays(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        ResponseEntity<RestBean> accessCheck = restrictAccess(userId);
        if (accessCheck != null) return accessCheck;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDay = now.minusDays(29).withHour(0).withMinute(0).withSecond(0).withNano(0);

        UserRegisterCountLineChart chart = new UserRegisterCountLineChart();
        chart.setCount(new int[30]);
        chart.setDays(new Date[30]);

        int totalUsers = 0;
        for (int i = 0; i < 30; i++) {
            LocalDateTime currentDay = startDay.plusDays(i);
            LocalDateTime nextDay = currentDay.plusDays(1);
            chart.getDays()[i] = Date.from(currentDay.atZone(ZoneId.systemDefault()).toInstant());
            chart.getCount()[i] = userService.getUserCountByTime(currentDay, nextDay.isAfter(now) ? now : nextDay);
            totalUsers += chart.getCount()[i];
        }

        System.out.println("过去30天的注册用户数量是:" + totalUsers);
        return RestBean.success(chart);
    }

    @GetMapping("/hotPostByDays")
    public ResponseEntity<RestBean> hotPostByDays(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        ResponseEntity<RestBean> accessCheck = restrictAccess(userId);
        if (accessCheck != null) return accessCheck;

        HotPostStatistic hotPostStatistic = new HotPostStatistic();
        LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime sevenDaysAgo = now.minusDays(6);

        int count = postService.getPostCountByTime(sevenDaysAgo, now);
        Object[][] hotPost = new Object[count][7];
        List<Post> posts = postService.getPostByTime(sevenDaysAgo, now);

        hotPostStatistic.setDate(new Date[7]);
        for (int i = 0; i < 7; i++) {
            hotPostStatistic.getDate()[i] = Date.from(sevenDaysAgo.plusDays(i).atZone(ZoneId.systemDefault()).toInstant());
        }

        for (int i = 0; i < count; i++) {
            Post post = posts.get(i);
            hotPost[i][0] = 6 - ChronoUnit.DAYS.between(post.getPostTime().toLocalDateTime(), now);
            hotPost[i][1] = post.getHot();
            hotPost[i][2] = experienceService.getLevel(post.getUserId());
            hotPost[i][3] = post.getBrowseCount();
            hotPost[i][4] = post.getLikeCount();
            hotPost[i][5] = post.getFloorCount();
            hotPost[i][6] = post.getTitle();
        }
        hotPostStatistic.setData(hotPost);
        return RestBean.success(hotPostStatistic);
    }
}