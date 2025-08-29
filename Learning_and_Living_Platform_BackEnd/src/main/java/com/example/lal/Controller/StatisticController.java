package com.example.lal.Controller;

import com.example.lal.model.RestBean;
import com.example.lal.model.domain.DownloadStatistic;
import com.example.lal.model.domain.Post;
import com.example.lal.model.entity.HotPostStatistic;
import com.example.lal.model.entity.OverallFigure;
import com.example.lal.model.entity.ResourceUploadsAndDownloadsLineChart;
import com.example.lal.model.entity.UserRegisterCountLineChart;
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
    @GetMapping("/getOverallFigure")
    public ResponseEntity<RestBean> getOverallFigure(HttpServletRequest request){
        String userId = request.getAttribute("userId").toString();
        if(Integer.parseInt(userId)>10000000||Integer.parseInt(userId)<=0){
            System.out.println("你没有权限查看统计信息");
            return RestBean.failure(HttpStatus.UNAUTHORIZED,"你没有权限查看统计信息");
        }
        OverallFigure overallFigure = new OverallFigure();
        overallFigure.setNumOfOnlineUser(userService.getAllUserOnlineCount());
        overallFigure.setNumOfUser(userService.getAllUserCount());
        overallFigure.setNumOfResource(resourceService.getAllResourceCount());
        overallFigure.setNumOfPost(postService.getAllPostCount());
        return RestBean.success(overallFigure);
    }

    //每subject，category的资源数量
    @GetMapping("/resourceClassificationStatistics")
    public ResponseEntity<RestBean> resourceClassificationStatistics(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        if(Integer.parseInt(userId)>10000000||Integer.parseInt(userId)<=0){
            System.out.println("你没有权限查看统计信息");
            return RestBean.failure(HttpStatus.UNAUTHORIZED,"你没有权限查看统计信息");
        }

        int[][] resourceNum = new int[CATEGORY_NUM][SUBJECT_NUM];
        for (int i = 0; i < CATEGORY_NUM; i++) {
            for (int j = 0; j < SUBJECT_NUM; j++) {
                int num = resourceService.getResourceNumByCategoryAndSubject(i, j);
                resourceNum[i][j] = num;
            }
        }
        return RestBean.success(resourceNum);
    }

    //展示过去30天的每天下载量
    @GetMapping("/resDownsAndUpsByDays")
    public ResponseEntity<RestBean> resourceDownloadsByDays(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        if(Integer.parseInt(userId)>10000000||Integer.parseInt(userId)<=0){
            System.out.println("你没有权限查看统计信息");
            return RestBean.failure(HttpStatus.UNAUTHORIZED,"你没有权限查看统计信息");
        }

        LocalDateTime now = LocalDateTime.now(); // 获取当前时间
        LocalDateTime oneWeekAgo = now.minusDays(29); //

        ResourceUploadsAndDownloadsLineChart resourceUploadsAndDownloadsLineChart = new ResourceUploadsAndDownloadsLineChart();
        resourceUploadsAndDownloadsLineChart.setDownloads(new int[30]);
        resourceUploadsAndDownloadsLineChart.setUploads(new int[30]);
        resourceUploadsAndDownloadsLineChart.setDays(new Date[30]);

        LocalDateTime currentDay = oneWeekAgo.withHour(0).withMinute(0).withSecond(0).withNano(0); // 将分钟、秒、纳秒设置为0，得到整小时的时间
        int i = 0;
        while (currentDay.isBefore(now)) {
            resourceUploadsAndDownloadsLineChart.getDays()[i] = Date
                    .from(currentDay.atZone(ZoneId.systemDefault()).toInstant());
            currentDay = currentDay.plusDays(1); // 增加一天
            if (currentDay.isAfter(now) || currentDay.equals(now)) {

                resourceUploadsAndDownloadsLineChart.getDownloads()[i] =
                        downloadHistoryService.getDownloadsByTime(currentDay.minusDays(1), LocalDateTime.now());
                resourceUploadsAndDownloadsLineChart.getUploads()[i] =
                        resourceService.getResourceCountByTime(currentDay.minusDays(1), LocalDateTime.now());
                break;
            }
            resourceUploadsAndDownloadsLineChart.getDownloads()[i] =
                    downloadHistoryService.getDownloadsByTime(currentDay.minusDays(1), currentDay);
            resourceUploadsAndDownloadsLineChart.getUploads()[i] =
                    resourceService.getResourceCountByTime(currentDay.minusDays(1), currentDay);
            i++;
        }
        System.out.println("过去一月的资源下载量是:" + downloadHistoryService.getDownloadsByTime(oneWeekAgo, now));
        return RestBean.success(resourceUploadsAndDownloadsLineChart);
    }

    //每个学科的资源量
    @GetMapping("/resourceCountBySubject")
    public ResponseEntity<RestBean> resourceCountBySubject(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        if(Integer.parseInt(userId)>10000000||Integer.parseInt(userId)<=0){
            System.out.println("你没有权限查看统计信息");
            return RestBean.failure(HttpStatus.UNAUTHORIZED,"你没有权限查看统计信息");
        }

        int[] resourceNum = new int[SUBJECT_NUM];
        for (int i = 0; i < SUBJECT_NUM; i++) {
            int num = resourceService.getResourceCountBySubject(i);
            resourceNum[i] = num;
        }
        return RestBean.success(resourceNum);
    }

    //过去30天的每天新增注册用户量
    @GetMapping("/userRegisterCountByDays")
    public ResponseEntity<RestBean> userRegisterCountByDays(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        if(Integer.parseInt(userId)>10000000||Integer.parseInt(userId)<=0){
            System.out.println("你没有权限查看统计信息");
            return RestBean.failure(HttpStatus.UNAUTHORIZED,"你没有权限查看统计信息");
        }

        LocalDateTime now = LocalDateTime.now(); // 获取当前时间
        LocalDateTime oneWeekAgo = now.minusDays(29); //
        UserRegisterCountLineChart userRegisterCountLineChart = new UserRegisterCountLineChart();
        userRegisterCountLineChart.setCount(new int[30]);
        userRegisterCountLineChart.setDays(new Date[30]);

        LocalDateTime currentDay = oneWeekAgo.withHour(0).withMinute(0).withSecond(0).withNano(0); // 将分钟、秒、纳秒设置为0，得到整小时的时间
        int i = 0;
        while (currentDay.isBefore(now)) {
            userRegisterCountLineChart.getDays()[i] = Date
                    .from(currentDay.atZone(ZoneId.systemDefault()).toInstant());
            currentDay = currentDay.plusDays(1); // 增加1天
            if (currentDay.isAfter(now) || currentDay.equals(now)) {
                userRegisterCountLineChart.getCount()[i] =
                        userService.getUserCountByTime(currentDay.minusDays(1), LocalDateTime.now());
                break;
            }
            userRegisterCountLineChart.getCount()[i] =
                    userService.getUserCountByTime(currentDay.minusDays(1), currentDay);
            i++;
        }
        System.out.println("过去30天的注册用户的数量是:" +downloadHistoryService.getDownloadsByTime(oneWeekAgo, now));
        return RestBean.success(userRegisterCountLineChart);
    }

    //过去七天的所有帖子的热度信息
    @GetMapping("/hotPostByDays")
    public ResponseEntity<RestBean> hotPostByDays(HttpServletRequest request) {
        String userId = request.getAttribute("userId").toString();
        if(Integer.parseInt(userId)>10000000||Integer.parseInt(userId)<=0){
            System.out.println("你没有权限查看统计信息");
            return RestBean.failure(HttpStatus.UNAUTHORIZED,"你没有权限查看统计信息");
        }

        HotPostStatistic hotPostStatistic = new HotPostStatistic();
        LocalDateTime now = LocalDateTime.now(); // 获取当前时间
        LocalDateTime sevenDaysAgo = now.minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0);

        int count = postService.getPostCountByTime(sevenDaysAgo, now);

        Object[][] hotPost = new Object[count][7];

        List<Post> posts = postService.getPostByTime(sevenDaysAgo, now);

        hotPostStatistic.setDate(new Date[7]);
        for(int i = 0;i < 7;i++)hotPostStatistic.getDate()[i]=Date
                .from(sevenDaysAgo.plusDays(i).atZone(ZoneId.systemDefault()).toInstant());

        for(int i = 0;i < count;i++){
            Post post = posts.get(i);
            hotPost[i][0] =6 - ChronoUnit.DAYS.between(post.getPostTime().toLocalDateTime(), now.withHour(0).withMinute(0).withSecond(0).withNano(0));
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