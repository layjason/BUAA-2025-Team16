package com.example.lal.Controller;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.lal.constant.OtherConstants;
import com.example.lal.constant.UserConstants;
import com.example.lal.model.entity.*;
import com.example.lal.model.RestBean;
import com.example.lal.model.exceptions.*;
import com.example.lal.model.request.post.*;
import com.example.lal.service.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {
    @Resource
    private PostService postService;

    @Resource
    private LikeService likeService;

    @Resource
    private OSSService ossService;

    @Resource
    private CommentService commentService;

    @Resource
    private ReplyService replyService;

    @Resource
    private UserService userService;

    @Resource
    private ExperienceService experienceService;

    @Resource
    private ExamineService examineService;


    @PostMapping("/uploadImage")
    public ResponseEntity<RestBean> uploadImage(@RequestPart("file") MultipartFile image,
                                                HttpServletRequest request){
        if(ObjectUtils.isEmpty(image) || image.getSize() <= 0){
            return RestBean.failure(HttpStatus.BAD_REQUEST, "未上传图片");
        }
        String imagePath = ossService.uploadFile(image, "0");
        System.out.println("uploadImage 调用成功");
        System.out.println(image);
        //错误处理
        if(imagePath == null){
            return RestBean.failure(HttpStatus.BAD_GATEWAY, "上传图片失败");
        }
        return RestBean.success(HttpStatus.CREATED, imagePath);
    }
    @PostMapping(value = "/postUpload")
    public ResponseEntity<RestBean> uploadPost(@RequestBody AddPostRequest addPostRequest, HttpServletRequest request) {
        System.out.println("uploadPost 调用成功");
        try{
            int userId = Integer.parseInt((String) request.getAttribute("userId"));
            if(userId <= 10000000){
                return RestBean.failure(HttpStatus.BAD_REQUEST, "当前身份无法发帖");
            }

            int level = experienceService.getLevel(userId);
            if(addPostRequest.getAuthority() > level){
                return RestBean.failure(HttpStatus.FORBIDDEN, "权限等级不足");
            }
//            System.out.println(userId);
            String reason=null;

            if(addPostRequest.getTitle() != null && addPostRequest.getTitle() != ""){
                reason = examineService.TextCensor(addPostRequest.getTitle());
                if(reason != null){
                    return RestBean.failure(HttpStatus.UNPROCESSABLE_ENTITY, reason);
                }
            } else {
                return RestBean.failure(HttpStatus.BAD_REQUEST, "未填写标题");
            }

            if(addPostRequest.getContent() != null && addPostRequest.getContent() != ""){
                reason = examineService.TextCensor(addPostRequest.getContent());
                if(reason != null){
                    return RestBean.failure(HttpStatus.UNPROCESSABLE_ENTITY, reason);
                }
            } else {
                return RestBean.failure(HttpStatus.BAD_REQUEST, "未填写内容");
            }

            List<String> stringList = addPostRequest.getImages();
            String commaSeparatedString;
            if(stringList.isEmpty()){
                commaSeparatedString = null;
            } else {
                commaSeparatedString = String.join(",", stringList);
            }

            boolean success = true;
            for(String path: stringList){
                success = ossService.submitFile(path);
                if(!success){
                    break;
                }
            }
            if(!success){
                for(String path: stringList){
                    ossService.rollbackFile(path);
                }
            }
            int postId = postService.addPost(addPostRequest, commaSeparatedString, userId);
            System.out.println(postId);
            experienceService.changeExp(String.valueOf(userId), 5);
//            postService.updateUpdateTime(postId);
            return RestBean.success("上传成功");
        } catch (PostException e) {
            System.out.println("uploadPost 调用失败");
            return RestBean.failure(500, null);
        }
    }

    @PostMapping("/postDetail")
    public ResponseEntity<RestBean> openPost(@RequestBody GetPostDetailRequest getPostDetailRequest, HttpServletRequest request){
        int postId = getPostDetailRequest.getPostId();
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        System.out.println("openPost 调用成功");
        try{
            PostDetail postDetail = postService.getPost(postId);
            Integer nowId = postService.getPost(postId).getUserId();
            if(nowId == null){
                postDetail.setUserId(UserConstants.DELETE_USER);
                postDetail.setProfilePhotoUrl(UserConstants.DELETE_AVATAR);
                postDetail.setUserName(UserConstants.DELETE_NAME);
                postDetail.setCanDelete(false);
                postDetail.setUserLevel(-1);
                postDetail.setUserLevelName(null);
            } else {
                postDetail.setUserName(userService.getUserInfo(String.valueOf(nowId)).getName());
                postDetail.setProfilePhotoUrl(userService.getUserInfo(String.valueOf(nowId)).getProfilePhotoUrl());
                int level = experienceService.getLevel(nowId);
                if(level < 0){
                    return RestBean.failure(HttpStatus.INTERNAL_SERVER_ERROR, "获取等级错误");
                }
                postDetail.setUserLevel(level);
                postDetail.setUserLevelName(experienceService.getLevelName(level));
                if(userId == nowId){
                    postDetail.setCanDelete(true);
                } else {
                    postDetail.setCanDelete(false);
                }
            }

            if(userId > 0 && userId <= 10000000){
                postDetail.setCanDelete(true);
                postDetail.setCanOperate(false);
            }

            ListCommentRequest listCommentRequest = new ListCommentRequest();
            listCommentRequest.setUserId(userId);
            listCommentRequest.setPostId(postId);
            listCommentRequest.setPageNum(1);
            listCommentRequest.setCntInPage(10);
            postDetail.setCommentList(commentService.getCommentList(listCommentRequest));

//            for(CommentEntry commentEntry: postDetail.getCommentList().getList()){
//                ListReplyRequest listReplyRequest = new ListReplyRequest();
//                listReplyRequest.setCommentId(commentEntry.getId());
//                listReplyRequest.setUserId(userId);
//                listReplyRequest.setCntInPage(3);
//                commentEntry.setReplyList(replyService.getReplyList(listReplyRequest));
//            }
            if(likeService.getLiked(userId, postId)){
                postDetail.setLike(true);
            } else {
                postDetail.setLike(false);
            }
            if(postDetail.getAuthority() > experienceService.getLevel(userId)&&userId>10000000){
                postDetail.setCanOperate(false);
                postDetail.setContent(OtherConstants.NO_AUTHORITY);
            } else {
               if(userId>10000000) {
                   experienceService.changeExp(String.valueOf(userId), 1);
                   postService.addBrowse(postId);
                   postDetail.setCanOperate(true);
               }else {
                   postDetail.setCanOperate(false);
               }
            }
            return RestBean.success(postDetail);
        } catch (PostException e){
            System.out.println("openPost 调用失败");
            return RestBean.failure(500,null);
        } catch (LikeException e) {
            return RestBean.failure(500, null);
        } catch (CommentException e) {
            throw new RuntimeException(e);
        } catch (UserException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/postList")
    public ResponseEntity<RestBean> listPost(@RequestBody ListPostRequest listPostRequest, HttpServletRequest request){
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        System.out.println("postList's userId" + userId);
        System.out.println("listPost 调用成功");
        try {
            Page<PostSummary> postSummaryPage = postService.getPostList(userId, listPostRequest);
            System.out.println(postSummaryPage);
            for(PostSummary postSummary: postSummaryPage.getList()){
                Integer nowId = postSummary.getUserId();
                if(nowId == null){
                    postSummary.setUserId(UserConstants.DELETE_USER);
                    postSummary.setProfilePhotoUrl(UserConstants.DELETE_AVATAR);
                    postSummary.setUserName(UserConstants.DELETE_NAME);
                    postSummary.setAuthority(-1);
                    postSummary.setUserLevelName(null);
                } else {
                    int level = experienceService.getLevel(nowId);
                    if(level < 0) {
                        return RestBean.failure(HttpStatus.INTERNAL_SERVER_ERROR, "获取等级失败");
                    }
                    postSummary.setAuthority(level);
                    postSummary.setUserName(userService.getUserInfo(String.valueOf(nowId)).getName());
                    postSummary.setProfilePhotoUrl(userService.getProfilePhotoUrl(nowId));
                    postSummary.setUserLevelName(experienceService.getLevelName(level));
                }
                if(userId > 0 && userId <= 10000000){
                    postSummary.setCanDelete(true);
                }
            }
            System.out.println("postList's postSummarypage "+postSummaryPage);
            return RestBean.success(postSummaryPage);
        } catch (PostException e){
            System.out.println("listPost 调用失败");
            //return null;
            return RestBean.failure(500,null);
        } catch (UserException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/postDelete")
    public ResponseEntity<RestBean> deletePost(@RequestBody DeletePostRequest deletePostRequest, HttpServletRequest request){
        int postId = deletePostRequest.getPostId();
        System.out.println(postId);
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        deletePostRequest.setUserId(userId);
        System.out.println(deletePostRequest.getUserId());
        System.out.println("deletePost 调用成功");
        try{
            PostDetail postDetail = postService.getPost(postId);
            //删post的image
            List<String> list = new ArrayList<>();
            System.out.println("imageUrl:"+postDetail.getImageurls());
            if(postDetail.getImageurls() != "" && postDetail.getImageurls() != null){
                list = Arrays.asList(postDetail.getImageurls().split(","));
            }
            for(String image: list){
                ossService.deleteFile(image);
            }
            // 设置Comment
            ListCommentRequest listCommentRequest = new ListCommentRequest();
            listCommentRequest.setUserId(userId);
            listCommentRequest.setPostId(postId);
            listCommentRequest.setPageNum(1);
            listCommentRequest.setCntInPage(10);
            postDetail.setCommentList(commentService.getCommentList(listCommentRequest));
            for(CommentEntry commentEntry: postDetail.getCommentList().getList()){
                List<String> list1 = new ArrayList<>();
                if(commentEntry.getImageUrl() != null){
                    list1 = Arrays.asList(commentEntry.getImageUrl().split(","));
                }
                for(String path: list1){
                    ossService.deleteFile(path);
                }
            }
            boolean isDelete = postService.deletePost(deletePostRequest);
            if (isDelete){
                experienceService.changeExp(String.valueOf(userId), -3);
                return RestBean.success(deletePostRequest.getPageNum());
            } else {
                return RestBean.failure(400, -1);
            }

        } catch (PostException e){
            System.out.println("openPost 调用失败");
            return RestBean.failure(500,-1);
        } catch (CommentException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/postLike")
    ResponseEntity<RestBean> likePost(@RequestBody LikePostRequest likePostRequest ,HttpServletRequest request) throws PostException {
        System.out.println("likePost 调用成功");
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        int postId = likePostRequest.getPostId();
        int level = experienceService.getLevel(userId);
        if(level < postService.getPost(postId).getAuthority()){
            return RestBean.failure(400, "权限不够");
        }
        System.out.println(likePostRequest);
        try {
            if(likePostRequest.getIsLike()){
                likeService.deleteLike(userId, postId);
                experienceService.changeExp(String.valueOf(userId), -1);
                return RestBean.success(-1);
            } else {
                likeService.addLike(userId, postId);
                experienceService.changeExp(String.valueOf(userId), 1);
                Integer ownerId = postService.getPost(postId).getUserId();
                if(ownerId != null){
                    experienceService.changeExp(String.valueOf(ownerId), 1);
                }
                return RestBean.success(1);
            }
        } catch (LikeException e) {
            //System.out.println(e);
            return RestBean.failure(500,0);
        }
    }

    @PostMapping("/postComment")
    public ResponseEntity<RestBean> commentPost(@RequestBody CommentPostRequest commentPostRequest, HttpServletRequest request) throws PostException {
        System.out.println("commentPost 调用成功");
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        int level = experienceService.getLevel(userId);
        if(level < postService.getPost(commentPostRequest.getPostId()).getAuthority()){
            return RestBean.failure(HttpStatus.FORBIDDEN, "权限不够");
        }
        String reason=null;
        if(commentPostRequest.getContent() != null && commentPostRequest.getContent() != ""){
            reason = ExamineService.TextCensor(commentPostRequest.getContent());
            if(reason != null){
                return RestBean.failure(HttpStatus.UNPROCESSABLE_ENTITY, reason);
            }
        } else {
            return RestBean.failure(HttpStatus.BAD_REQUEST, "未填写内容");
        }
        commentPostRequest.setUserId(userId);
        try{
            List<String> stringList = commentPostRequest.getImageUrl();
            String commaSeparatedString;
            if(stringList.isEmpty()){
                commaSeparatedString = null;
            } else {
                commaSeparatedString = String.join(",", stringList);
            }
            if(commentService.addComment(commentPostRequest, commaSeparatedString)){
                for(String path: stringList){
                    ossService.submitFile(path);
                }
                experienceService.changeExp(String.valueOf(userId), 3);
                postService.updateUpdateTime(commentPostRequest.getPostId());
                return RestBean.success("发帖成功");
            } else {
                return RestBean.success(-1);
            }
        } catch (CommentException e){
            System.out.println(e);
            return RestBean.failure(500, 0);
        }

    }

    @PostMapping("/commentList")
    public ResponseEntity<RestBean> listComment(@RequestBody ListCommentRequest listCommentRequest, HttpServletRequest request){
        System.out.println("commentList 调用成功");
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        System.out.println(listCommentRequest);
        listCommentRequest.setUserId(userId);
        try {
            Page<CommentEntry> commentEntryPage = commentService.getCommentList(listCommentRequest);
//            for(CommentEntry commentEntry: commentEntryPage.getList()){
//                Integer nowId = commentEntry.getUserId();
//                System.out.println(nowId);
//                ListReplyRequest listReplyRequest = new ListReplyRequest();
//                listReplyRequest.setCommentId(commentEntry.getId());
//                if(nowId == null){
//                    commentEntry.setUserLevel(UserConstants.DELETE_AUTHORITY);
//                    commentEntry.setUserLevelName(null);
//                } else {
//                    int level = experienceService.getLevel(nowId);
//                    if(level < 0){
//                        return RestBean.failure(HttpStatus.INTERNAL_SERVER_ERROR, "等级获取失败");
//                    }
//                    commentEntry.setUserLevel(level);
//                    commentEntry.setUserLevelName(experienceService.getLevelName(level));
//                }
//                listReplyRequest.setUserId(userId);
//                listReplyRequest.setCntInPage(3);
//                commentEntry.setReplyList(replyService.getReplyList(listReplyRequest));
//            }
            return RestBean.success(commentEntryPage);
        }  catch (CommentException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/replyList")
    public ResponseEntity<RestBean> listReply(@RequestBody ListReplyRequest listReplyRequest, HttpServletRequest request){
        System.out.println("cReplyList 调用成功");
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        System.out.println(listReplyRequest);
        listReplyRequest.setUserId(userId);
        try {
            List<ReplyEntry> replyEntryPage = replyService.getReplyList(listReplyRequest);
            return RestBean.success(replyEntryPage);
        }  catch (ReplyException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/commentReply")
    public ResponseEntity<RestBean> replyComment(@RequestBody ReplyCommentRequest replyCommentRequest, HttpServletRequest request) throws PostException {
        System.out.println("replyComment 调用成功");
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        int level = experienceService.getLevel(userId);
        if(level < postService.getPost(replyCommentRequest.getPostId()).getAuthority()){
            return RestBean.failure(400, "权限不够");
        }
        String reason=null;
        if(replyCommentRequest.getContent() != null && replyCommentRequest.getContent() != ""){
            reason = examineService.TextCensor(replyCommentRequest.getContent());
            if(reason != null){
                return RestBean.failure(HttpStatus.UNPROCESSABLE_ENTITY, reason);
            }
        } else {
            return RestBean.failure(HttpStatus.BAD_REQUEST, "未填写内容");
        }
        replyCommentRequest.setUserId(userId);
        try{
            ReplyEntry replyEntry = replyService.addReply(replyCommentRequest);
            if(replyEntry != null){
                experienceService.changeExp(String.valueOf(userId), 1);
                postService.updateUpdateTime(replyCommentRequest.getPostId());
                return RestBean.success(replyEntry);
            } else {
                return RestBean.failure(500, null);
            }
        } catch (ReplyException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/commentDelete")
    public ResponseEntity<RestBean> deleteComment(@RequestBody DeleteCommentRequest deleteCommentRequest, HttpServletRequest request){
        int commentId = deleteCommentRequest.getCommentId();
        System.out.println(commentId);
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        System.out.println("deleteComment 调用成功");
        try{
            String image = commentService.getComment(commentId).getImageUrl();
            boolean isDelete = commentService.deleteComment(deleteCommentRequest, userId);
            if (isDelete){
                List<String> list = new ArrayList<>();
                if(image != null && image != ""){
                    list = Arrays.asList(image.split(","));
                }

                for(String path: list){
                    ossService.deleteFile(path);
                }
                experienceService.changeExp(String.valueOf(userId), -2);
                return RestBean.success(1);
            } else {
                return RestBean.failure(400, -1);
            }

        } catch (CommentException e) {
            throw new RuntimeException(e);
        }
    }

    @DeleteMapping("/replyDelete")
    public ResponseEntity<RestBean> deleteReply(@RequestBody DeleteReplyRequest deleteReplyRequest, HttpServletRequest request){
        int replyId = deleteReplyRequest.getReplyId();
        System.out.println(replyId);
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        System.out.println("deleteReply 调用成功");
        try{
            boolean isDelete = replyService.deleteReply(deleteReplyRequest, userId);
            if (isDelete){
                experienceService.changeExp(String.valueOf(userId), -1);
                return RestBean.success(1);
            } else {
                return RestBean.failure(400, -1);
            }

        } catch (ReplyException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/getHotPost")
    public ResponseEntity<RestBean> getPostByHot(HttpServletRequest request){
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        System.out.println("getPostByHot 调用成功");

        List<PostSummary> postSummaryList = new ArrayList<>();
        try {
            postSummaryList = postService.getPostByHot(userId);
            for(PostSummary postSummary: postSummaryList){
                Integer nowId = postSummary.getUserId();
                if(nowId == null){
                    postSummary.setUserId(UserConstants.DELETE_USER);
                    postSummary.setProfilePhotoUrl(UserConstants.DELETE_AVATAR);
                    postSummary.setUserName(UserConstants.DELETE_NAME);
                    postSummary.setAuthority(-1);
                    postSummary.setUserLevelName(null);
                } else {
                    int level = experienceService.getLevel(nowId);
                    if(level < 0) {
                        return RestBean.failure(HttpStatus.INTERNAL_SERVER_ERROR, "获取等级失败");
                    }   
                    postSummary.setAuthority(level);
                    postSummary.setUserName(userService.getUserInfo(String.valueOf(nowId)).getName());
                    postSummary.setProfilePhotoUrl(userService.getProfilePhotoUrl(nowId));
                    postSummary.setUserLevelName(experienceService.getLevelName(level));
                }
                if(userId > 0 && userId <= 10000000){
                    postSummary.setCanDelete(true);
                } else {
                    postSummary.setCanDelete(false);
                }
            }
            // System.out.println(postSummaryList);
            return RestBean.success(postSummaryList);
        } catch (PostException e) {
            throw new RuntimeException(e);
        } catch (UserException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/getUserPost")
    public ResponseEntity<RestBean> getUserPost(@RequestBody ListUserPostRequest listUserPostRequest, HttpServletRequest request){
        String token = request.getHeader("token");
        String userId;
        if(token == null||token.equals("")){
            userId = "0";
        }else {
            try {
                DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
                System.out.println("token中得到的userId:"+JwtUtil.getUserId(token));
                userId = JwtUtil.getUserId(token);
            } catch (Exception e) {
                return RestBean.failure(HttpStatus.UNAUTHORIZED,"Invalid or expired token");
            }
        }
//        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        System.out.println("getUserPost 调用成功");

        try {
            Page<PostSummary> postSummaryPage = postService.postsOfUser(listUserPostRequest);
            return RestBean.success(postSummaryPage);
        } catch (PostException e) {
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

}
