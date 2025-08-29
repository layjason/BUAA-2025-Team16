package com.example.lal.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.lal.model.RestBean;
import com.example.lal.model.entity.*;
import com.example.lal.model.exceptions.ResourceException;
import com.example.lal.model.request.resource.*;
import com.example.lal.service.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/resource")
public class ResourceController {
    @Resource
    private ResourceService resourceService;
    @Resource
    private ExperienceService experienceService;
    @Resource
    private OSSService ossService;
    @Resource
    private UserService userService;
    @Resource
    private DownloadHistoryService downloadHistoryService;

    @Resource
    private ExamineService examineService;

    @PostMapping("/uploadFile")
    public ResponseEntity<RestBean> uploadFile(@RequestPart("file") MultipartFile file,
            HttpServletRequest request) {
        if (ObjectUtils.isEmpty(file) || file.getSize() <= 0) {
            return RestBean.failure(HttpStatus.BAD_REQUEST, "未上传资源");
        }
        String filePath = ossService.uploadFile(file, "resource/file");
        System.out.println("uploadFile 调用成功");
        // 错误处理
        if (filePath == null) {
            return RestBean.failure(HttpStatus.BAD_GATEWAY, "上传文件失败");
        }
        return RestBean.success(HttpStatus.CREATED, filePath);
    }

    @PostMapping("/uploadImage")
    public ResponseEntity<RestBean> uploadImage(@RequestPart("file") MultipartFile image,
            HttpServletRequest request) {
        if (ObjectUtils.isEmpty(image) || image.getSize() <= 0) {
            return RestBean.failure(HttpStatus.BAD_REQUEST, "未上传图片");
        }
        String imagePath = ossService.uploadFile(image, "resource/image");
        System.out.println("uploadImage 调用成功");
        // 错误处理
        if (imagePath == null) {
            return RestBean.failure(HttpStatus.BAD_GATEWAY, "上传图片失败");
        }
        return RestBean.success(HttpStatus.CREATED, imagePath);
    }

    @PostMapping("/uploadResource")
    public ResponseEntity<RestBean> uploadResource(@RequestBody UploadResourceRequest uploadResourceRequest,
            HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            System.out.println("uploadResource 调用成功");

            String reason = null;

            if (uploadResourceRequest.getTitle() != null && uploadResourceRequest.getTitle() != "") {
                reason = examineService.TextCensor(uploadResourceRequest.getTitle());
                if (reason != null) {
                    return RestBean.failure(HttpStatus.UNPROCESSABLE_ENTITY, reason);
                }
            } else {
                return RestBean.failure(HttpStatus.BAD_REQUEST, "未填写标题");
            }

            if (uploadResourceRequest.getContent() != null && uploadResourceRequest.getContent() != "") {
                reason = examineService.TextCensor(uploadResourceRequest.getContent());
                if (reason != null) {
                    return RestBean.failure(HttpStatus.UNPROCESSABLE_ENTITY, reason);
                }
            } else {
                return RestBean.failure(HttpStatus.BAD_REQUEST, "未填写内容");
            }

            boolean canUpload = true;
            if (uploadResourceRequest.getImagePath() != null) {
                canUpload &= ossService.submitFile(uploadResourceRequest.getImagePath());
            }
            if (uploadResourceRequest.getFilePath() != null) {
                canUpload &= ossService.submitFile(uploadResourceRequest.getFilePath());
            }
            if (canUpload) {
                resourceService.addResource(userId, uploadResourceRequest);
                if (Integer.parseInt(userId) > 10000000) {
                    experienceService.changeExp(userId, 8);
                }

                return RestBean.success("上传成功");
            } else {
                return RestBean.failure(HttpStatus.BAD_REQUEST, "由于您过长时间未操作，需要重新上传资源");
            }

        } catch (ResourceException e) {
            return RestBean.failure(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/getResDetail")
    public ResponseEntity<RestBean> getResourceDetail(@RequestBody GetResourceDetailRequest getResourceDetailRequest,
            HttpServletRequest request) {
        try {
            String token = request.getHeader("token");
            String userId;
            if (token == null || token.equals("")) {
                userId = "0";
            } else {
                try {
                    DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
                    System.out.println("token中得到的userId:" + JwtUtil.getUserId(token));
                    userId = JwtUtil.getUserId(token);
                } catch (Exception e) {
                    return RestBean.failure(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
                }
            }
            System.out.println("getResDetail 调用成功");
            ResourceDetail resourceDetail = resourceService.getResourceDetail(getResourceDetailRequest);
            resourceDetail.setCanDelete(userId.equals(resourceDetail.getUserId()));
            resourceDetail.setPath(resourceDetail.getPath());
            
            int id = Integer.parseInt(userId);
            if (id < 10000000 && id > 0) {
                resourceDetail.setCanDelete(true);
                resourceDetail.setCanModify(true);
            } else {
                if (userId.equals(resourceDetail.getUserId()))
                resourceDetail.setCanDelete(true);
            }
            resourceDetail.setCanDownload(id != 0);
            System.out.println(resourceDetail+"from getresdetail backend");
            return RestBean.success(resourceDetail);
        } catch (ResourceException e) {
            return RestBean.failure(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    // ResourceSummary
    @PutMapping("/updateResource")
    public ResponseEntity<RestBean> updateResource(@RequestBody UpdateResourceRequest updateResourceRequest,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        GetResourceDetailRequest getResourceDetailRequest = new GetResourceDetailRequest();
        getResourceDetailRequest.setResourceId(updateResourceRequest.getId());
        try {
            ResourceDetail resourceDetail = resourceService.getResourceDetail(getResourceDetailRequest);
            if (Integer.parseInt(userId) > 10000000 && !Objects.equals(resourceDetail.getUserId(), userId)) {
                System.out.println("你没有权限进行更新资源操作！");
                return RestBean.failure(HttpStatus.UNAUTHORIZED, "你没有权限进行更新资源操作！");
            }
            String title = updateResourceRequest.getTitle();
            String content = updateResourceRequest.getContent();

            String reason = null;

            if (title != null && title != "") {
                reason = examineService.TextCensor(title);
                if (reason != null) {
                    return RestBean.failure(HttpStatus.UNPROCESSABLE_ENTITY, reason);
                }
            } else {
                return RestBean.failure(HttpStatus.BAD_REQUEST, "未填写标题");
            }

            if (content != null && content != "") {
                reason = examineService.TextCensor(content);
                if (reason != null) {
                    return RestBean.failure(HttpStatus.UNPROCESSABLE_ENTITY, reason);
                }
            } else {
                return RestBean.failure(HttpStatus.BAD_REQUEST, "未填写内容");
            }

            int subject = updateResourceRequest.getSubject();
            int[] categories = updateResourceRequest.getCategories();

            if (title.length() > 20 || content.length() > 500) {
                return RestBean.failure(HttpStatus.BAD_REQUEST, "标题或内容过长！");
            }
            resourceDetail.setTitle(title);
            resourceDetail.setSubject(subject);
            resourceDetail.setCategories(categories);
            resourceDetail.setContent(content);
            resourceService.updateResourceDetail(resourceDetail);
            resourceService.updateResourceCategories(resourceDetail.getId(), categories);
            return RestBean.success(resourceDetail);
        } catch (ResourceException e) {
            return RestBean.success(null);
        }

    }

    @DeleteMapping("/deleteResource")
    public ResponseEntity<RestBean> deleteResource(@RequestBody DeleteResourceRequest deleteResourceRequest,
            HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        GetResourceDetailRequest getResourceDetailRequest = new GetResourceDetailRequest();
        getResourceDetailRequest.setResourceId(deleteResourceRequest.getResourceId());
        try {
            ResourceDetail resourceDetail = resourceService.getResourceDetail(getResourceDetailRequest);
            if (Integer.parseInt(userId) > 10000000 && !userId.equals(resourceDetail.getUserId())) {
                return RestBean.failure(401, "无权限删除");
            }
            if (resourceDetail.getImageUrl() != null) {
                ossService.deleteFile(resourceDetail.getImageUrl());
            }
            if (resourceDetail.getPath() != null) {
                ossService.deleteFile(resourceDetail.getPath());
            }
            Integer ownerId = Integer.parseInt(resourceDetail.getUserId());
            if (ownerId > 10000000 && ownerId != null) {
                experienceService.changeExp(String.valueOf(ownerId), -5);
            }
            resourceService.deleteResource(deleteResourceRequest.getResourceId());
            return RestBean.success("删除成功");
        } catch (ResourceException e) {
            return RestBean.failure(404, "资源不存在");
        }
    }

    @PostMapping("/listResByClassWithPage")
    public ResponseEntity<RestBean> listResourceByClassWithPage(
            @RequestBody ListResourceByCategoryRequest listResourceByCategoryRequest,
            HttpServletRequest request) {
        Page<ResourceSummary> resourceSummaryPage = resourceService
                .getResourceSummaryByClassWithPage(listResourceByCategoryRequest);
        String token = request.getHeader("token");
        String userId;
        if (token == null || token.equals("")) {
            userId = "0";
        } else {
            try {
                DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
                System.out.println("token中得到的userId:" + JwtUtil.getUserId(token));
                userId = JwtUtil.getUserId(token);
            } catch (Exception e) {
                return RestBean.failure(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }
        }
        for (ResourceSummary resourceSummary : resourceSummaryPage.getList()) {
            if (!(Integer.parseInt(userId) < 99999999 && Integer.parseInt(userId) >= 0)) {
                return RestBean.failure(HttpStatus.BAD_REQUEST, "资源列表获取失败");
            }
            if ((Integer.parseInt(userId) < 10000000 && Integer.parseInt(userId) > 0)
                    || userId.equals(resourceSummary.getUserId())) {
                resourceSummary.setCanDelete(true);
            } else {
                resourceSummary.setCanDelete(false);
            }
        }
        return RestBean.success(resourceSummaryPage);
        // return RestBean.generate(null);
    }

    @PostMapping("/downloadResource")
    public ResponseEntity<RestBean> downloadResource(@RequestBody DownloadResourceRequest downloadResourceRequest,
            HttpServletRequest request) {
        try {
            String userId = (String) request.getAttribute("userId");
            String resourceId = downloadResourceRequest.getResourceId();
            GetResourceDetailRequest getResourceDetailRequest = new GetResourceDetailRequest();
            getResourceDetailRequest.setResourceId(resourceId);
            System.out.println("downloadResource 调用成功");
            System.out.println("下载的resourceId:" + resourceId);
            ResourceDetail resourceDetail = resourceService.getResourceDetail(getResourceDetailRequest);

            String resourceTitle = resourceDetail.getTitle();
            String fileName = resourceDetail.getFileName();
            resourceDetail.setDownloadCount(resourceDetail.getDownloadCount() + 1);
            resourceService.updateResourceDetail(resourceDetail);
            if (!downloadHistoryService.addDownloadHistory(userId, resourceId, resourceTitle, fileName)) {
                return RestBean.failure(HttpStatus.INTERNAL_SERVER_ERROR, "下载历史存储失败");
            }

            DownloadFileEntry downloadFileEntry = new DownloadFileEntry();
            downloadFileEntry.setPath(resourceDetail.getPath());
            downloadFileEntry.setFileName(resourceDetail.getFileName());

            Integer ownerId = Integer.valueOf(resourceDetail.getUserId());
            if (ownerId != null && ownerId > 10000000) {
                experienceService.changeExp(String.valueOf(ownerId), 2);
            }
            return RestBean.success(downloadFileEntry);
        } catch (ResourceException e) {
            return RestBean.failure(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/listDownloadHistoryByUserId")
    public ResponseEntity<RestBean> listDownloadHistoryByUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        List<DownloadHistoryEntry> downloadHistoryList = downloadHistoryService.listDownloadHistoryByUserId(userId);
        return RestBean.success(downloadHistoryList);
    }

    @PostMapping("/searchResource")
    public ResponseEntity<RestBean> searchResource(@RequestBody SearchResourceRequest searchResourceRequest,
            HttpServletRequest request) {
        String token = request.getHeader("token");
        String userId;
        if (token == null || token.equals("")) {
            userId = "0";
        } else {
            try {
                DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
                System.out.println("token中得到的userId:" + JwtUtil.getUserId(token));
                userId = JwtUtil.getUserId(token);
            } catch (Exception e) {
                return RestBean.failure(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }
        }
        System.out.println("subjects: " + Arrays.toString(searchResourceRequest.getSubjects()));
        System.out.println("categories: " + Arrays.toString(searchResourceRequest.getCategories()));

        Page<ResourceSummary> resourceSummaryPage = resourceService.searchResource(searchResourceRequest);
        for (ResourceSummary resourceSummary : resourceSummaryPage.getList()) {
            if (!(Integer.parseInt(userId) < 99999999 && Integer.parseInt(userId) >= 0)) {
                return RestBean.failure(HttpStatus.BAD_REQUEST, "资源列表获取失败");
            }
            if ((Integer.parseInt(userId) < 10000000 && Integer.parseInt(userId) > 0)
                    || userId.equals(resourceSummary.getUserId())) {
                resourceSummary.setCanDelete(true);
            } else {
                resourceSummary.setCanDelete(false);
            }
        }
        return RestBean.success(resourceSummaryPage);
    }

    @PostMapping("/listResourceByUserId")
    public ResponseEntity<RestBean> listResourceByUserId(
            @RequestBody ListResourceByUserIdRequest listResourceByUserIdRequest,
            HttpServletRequest request) {
        String userId = listResourceByUserIdRequest.getUserId();
        String requestId = (String) request.getAttribute("userId");
        Page<ResourceSummary> resourceSummaryPage = resourceService
                .getResourceSummaryByUserIdWithPage(listResourceByUserIdRequest);
        if (userId.equals(requestId))
            for (ResourceSummary resourceSummary : resourceSummaryPage.getList()) {
                resourceSummary.setCanDelete(true);
            }
        else
            for (ResourceSummary resourceSummary : resourceSummaryPage.getList()) {
                resourceSummary.setCanDelete(false);
            }
        return RestBean.success(resourceSummaryPage);
    }

    @GetMapping("/listResourceRecommend")
    public ResponseEntity<RestBean> listResourceRecommend(HttpServletRequest request) {
        System.out.println("资源推荐调用成功");
        int N = userService.getMaxUserId() - 10000000 + 1;
        int[][] sparseMatrix = new int[N][N];// 建立用户稀疏矩阵，用于用户相似度计算【相似度矩阵】
        Map<Integer, Integer> userDownloads = downloadHistoryService.getUserResourceMap();
        ;// 存储每一个用户对应的不同物品总数 eg: A 3
        // 遍历userDownloads
        for (Map.Entry<Integer, Integer> entry : userDownloads.entrySet()) {
            System.out.println(entry.getKey() + " " + entry.getValue());
        }
        List<Integer> resourceIdList = downloadHistoryService.listAllResourceId();// 下载记录中所有的资源id

        for (Integer resourceId : resourceIdList) {
            // 下载过这个资源的所有用户id
            List<Integer> uerIdList = downloadHistoryService.listDownloadHistoryUserIdByResourceId(resourceId);
            for (Integer userId1 : uerIdList) {
                for (Integer userId2 : uerIdList) {
                    if (!Objects.equals(userId1, userId2)) {
                        sparseMatrix[userId1 - 10000000][userId2 - 10000000] += 1;
                    }
                }
            }
        }
        String recommendUser = (String) request.getAttribute("userId");
        if (Integer.parseInt(recommendUser) < 10000000) {
            System.out.println("管理员没有自己的推荐资源");
            return RestBean.failure(HttpStatus.UNAUTHORIZED, "管理员没有自己的推荐资源");
        }
        int recommendUserId = Integer.parseInt(recommendUser) - 10000000;
        System.out.println(userDownloads.get(recommendUserId + 10000000));
        for (int j = 0; j < sparseMatrix.length; j++) {
            // System.out.println(j);
            if (j != recommendUserId) {
                if (userDownloads.get(j + 10000000) != null && userDownloads.get(recommendUserId + 10000000) != null)
                    System.out.println("用户" +
                            recommendUserId + 10000000 +
                            "--和--用户" + (j + 10000000) +
                            "的相似度:" +
                            sparseMatrix[recommendUserId][j]
                                    / Math.sqrt(
                                            userDownloads.get(recommendUserId + 10000000)
                                                    * userDownloads.get(j + 10000000)));
            }
        }
        List<ResourceRecommend> resourceRecommendList = new ArrayList<>();
        // List<Integer> resourceIdList =
        // downloadHistoryService.listAllResourceId();//下载记录中所有的资源id
        for (Integer resourceId : resourceIdList) {
            // 下载过这个资源的所有用户id
            List<Integer> userIdList = downloadHistoryService.listDownloadHistoryUserIdByResourceId(resourceId);
            if (!userIdList.contains(recommendUserId + 10000000)) {
                double itemRecommendDegree = 0.0;
                for (Integer user : userIdList) {
                    itemRecommendDegree += sparseMatrix[recommendUserId][user - 10000000]
                            / Math.sqrt(userDownloads.get(recommendUserId + 10000000)
                                    * userDownloads.get(user));// 推荐度计算
                }
                ResourceRecommend resourceRecommend = new ResourceRecommend();
                resourceRecommend.setResourceId(resourceId);
                resourceRecommend.setRecommendDegree(itemRecommendDegree);
                resourceRecommendList.add(resourceRecommend);
                System.out.println("The resource " + resourceId + " for " + recommendUser + "'s recommended degree:"
                        + itemRecommendDegree);
            }
        }
        // 计算指定用户recommendUser的resource推荐度
        // scanner.close();
        // 按推荐度排序
        Collections.sort(resourceRecommendList, new Comparator<ResourceRecommend>() {
            @Override
            public int compare(ResourceRecommend o1, ResourceRecommend o2) {
                if (o1.getRecommendDegree() < o2.getRecommendDegree())
                    return 1;
                else if (o1.getRecommendDegree() == o2.getRecommendDegree() && o1.getResourceId() > o2.getResourceId())
                    return 1;
                return -1;

            }
        });
        while (resourceRecommendList.size() > 15)
            resourceRecommendList.remove(resourceRecommendList.size() - 1);
        List<ResourceSummary> resourceSummaryList = new ArrayList<>();
        List<Integer> resourceIDs = new ArrayList<>();
        for (ResourceRecommend resourceRecommend : resourceRecommendList) {
            ResourceSummary resourceSummary = resourceService.getResourceSummary(resourceRecommend.getResourceId());
            resourceSummaryList.add(resourceSummary);
            resourceIDs.add(resourceRecommend.getResourceId());
        }
        if (resourceRecommendList.size() < 15) {
            int size = 15 - resourceRecommendList.size();
            List<ResourceSummary> randomResourceList = resourceService.getResourceSummaryListRandom(size, resourceIDs);
            resourceSummaryList.addAll(randomResourceList);
        }
        return RestBean.success(resourceSummaryList);
    }
}
