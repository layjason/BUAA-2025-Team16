package com.example.lal.Controller;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.lal.LalApplication;
import com.example.lal.model.entity.*;
import com.example.lal.model.exceptions.CommentException;
import com.example.lal.model.exceptions.PostException;
import com.example.lal.model.exceptions.ReplyException;
import com.example.lal.model.request.post.*;
import com.example.lal.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LalApplication.class)
@AutoConfigureMockMvc
class PostControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockBean private PostService postService;
    @MockBean private LikeService likeService;
    @MockBean private OSSService ossService;
    @MockBean private CommentService commentService;
    @MockBean private ReplyService replyService;
    @MockBean private UserService userService;
    @MockBean private ExperienceService experienceService;
    @MockBean private ExamineService examineService;

    @Test
    void uploadImage_positive_successfulUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        when(ossService.uploadFile(any(MultipartFile.class), eq("0"))).thenReturn("/path/test.jpg");

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedUserIdClaim = mock(Claim.class);
            when(mockedJWT.getClaim("userId")).thenReturn(mockedUserIdClaim);
            when(mockedUserIdClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));

            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            mockMvc.perform(multipart("/post/uploadImage")
                            .file(file)
                            .header("token", "mock-token"))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.message").value("/path/test.jpg"));
        }

        verify(ossService).uploadFile(any(MultipartFile.class), eq("0"));
    }

    @Test
    void uploadImage_negative_emptyFile() throws Exception {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "", "image/jpeg", new byte[0]);

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedUserIdClaim = mock(Claim.class);
            when(mockedJWT.getClaim("userId")).thenReturn(mockedUserIdClaim);
            when(mockedUserIdClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));

            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            mockMvc.perform(multipart("/post/uploadImage")
                            .file(emptyFile)
                            .header("token", "mock-token"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("未上传图片"));
        }

        verify(ossService, never()).uploadFile(any(MultipartFile.class), anyString());
    }

    @Test
    void uploadPost_positive_successfulUpload() throws Exception {
        AddPostRequest req = new AddPostRequest();
        req.setTitle("Title");
        req.setContent("Content");
        req.setAuthority(1);
        req.setImages(List.of("img1.jpg"));

        when(experienceService.getLevel(anyInt())).thenReturn(2);
        when(ossService.submitFile(anyString())).thenReturn(true);
        when(postService.addPost(any(AddPostRequest.class), anyString(), anyInt())).thenReturn(1);
        when(experienceService.changeExp(eq("10000001"), anyInt())).thenReturn(true);

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedUserIdClaim = mock(Claim.class);
            when(mockedJWT.getClaim("userId")).thenReturn(mockedUserIdClaim);
            when(mockedUserIdClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            mockMvc.perform(post("/post/postUpload")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))
                            .header("token", "mock-token")
                            .requestAttr("userId", "10000001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("上传成功"));
        }

        verify(postService).addPost(any(AddPostRequest.class), anyString(), eq(10000001));
    }

    @Test
    void uploadPost_negative_noTitle() throws Exception {
        AddPostRequest req = new AddPostRequest();
        req.setTitle("");
        req.setContent("Content");
        req.setAuthority(1);
        req.setImages(List.of("img1.jpg"));

        when(experienceService.getLevel(anyInt())).thenReturn(2);

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedUserIdClaim = mock(Claim.class);
            when(mockedJWT.getClaim("userId")).thenReturn(mockedUserIdClaim);
            when(mockedUserIdClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));

            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            mockMvc.perform(post("/post/postUpload")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))
                            .header("token", "mock-token")
                            .requestAttr("userId", "10000001"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("未填写标题"));
        }
    }

    @Test
    void openPost() throws Exception {
        GetPostDetailRequest req = new GetPostDetailRequest();
        req.setPostId(1);

        PostDetail post = new PostDetail();
        post.setId(1);
        post.setUserId(10000001);
        post.setAuthority(1);
        post.setImageurls("img1.jpg");

        UserDetail user = new UserDetail();
        user.setName("User");
        user.setProfilePhotoUrl("photo.jpg");

        Page<CommentEntry> comments = new Page<>();

        when(postService.getPost(eq(1))).thenReturn(post);
        when(userService.getUserInfo(eq("10000001"))).thenReturn(user);
        when(experienceService.getLevel(anyInt())).thenReturn(2);
        when(experienceService.getLevelName(eq(2))).thenReturn("Level 2");
        when(likeService.getLiked(eq(10000001), eq(1))).thenReturn(true);
        when(commentService.getCommentList(any(ListCommentRequest.class))).thenReturn(comments);
        when(experienceService.changeExp(eq("10000001"), anyInt())).thenReturn(true);
        doNothing().when(postService).addBrowse(eq(1));

        mockMvc.perform(post("/post/postDetail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
        verify(postService, times(2)).getPost(eq(1));

        when(postService.getPost(eq(1))).thenThrow(new PostException("Not found"));
        mockMvc.perform(post("/post/postDetail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void listPost() throws Exception {
        ListPostRequest req = new ListPostRequest();
        Page<PostSummary> page = new Page<>();
        PostSummary summary = new PostSummary();
        summary.setUserId(10000001);
        page.setList(List.of(summary));

        when(postService.getPostList(eq(10000001), any(ListPostRequest.class))).thenReturn(page);

        UserDetail user = new UserDetail();
        user.setName("User");
        user.setProfilePhotoUrl("photo.jpg");
        when(userService.getUserInfo(anyString())).thenReturn(user);
        when(experienceService.getLevel(anyInt())).thenReturn(2);
        when(experienceService.getLevelName(eq(2))).thenReturn("Level 2");

        mockMvc.perform(post("/post/postList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.list[0].userName").value("User"));
        verify(postService).getPostList(eq(10000001), any(ListPostRequest.class));

        when(postService.getPostList(eq(10000001), any(ListPostRequest.class))).thenThrow(new PostException("Error"));
        mockMvc.perform(post("/post/postList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deletePost() throws Exception {
        DeletePostRequest req = new DeletePostRequest();
        req.setPostId(1);

        PostDetail post = new PostDetail();
        post.setImageurls("img1.jpg");

        Page<CommentEntry> comments = new Page<>();
        CommentEntry comment = new CommentEntry();
        comment.setImageUrl("comment_img.jpg");
        comments.setList(List.of(comment));

        when(postService.getPost(eq(1))).thenReturn(post);
        when(commentService.getCommentList(any(ListCommentRequest.class))).thenReturn(comments);
        when(postService.deletePost(any(DeletePostRequest.class))).thenReturn(true);
        when(experienceService.changeExp(eq("10000001"), anyInt())).thenReturn(true);
        doNothing().when(ossService).deleteFile(anyString());

        mockMvc.perform(delete("/post/postDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(postService).deletePost(any(DeletePostRequest.class));

        when(postService.deletePost(any(DeletePostRequest.class))).thenReturn(false);
        mockMvc.perform(delete("/post/postDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void likePost() throws Exception {
        LikePostRequest req = new LikePostRequest();
        req.setPostId(1);
        req.setIsLike(false);

        PostDetail post = new PostDetail();
        post.setAuthority(1);
        post.setUserId(10000002);

        when(postService.getPost(eq(1))).thenReturn(post);
        when(experienceService.getLevel(anyInt())).thenReturn(2);
        when(likeService.addLike(eq(10000001), eq(1))).thenReturn(true);
        when(experienceService.changeExp(anyString(), anyInt())).thenReturn(true);

        mockMvc.perform(post("/post/postLike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(likeService).addLike(eq(10000001), eq(1));

        when(experienceService.getLevel(anyInt())).thenReturn(0);
        mockMvc.perform(post("/post/postLike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void commentPost() throws Exception {
        CommentPostRequest req = new CommentPostRequest();
        req.setPostId(1);
        req.setContent("Comment");
        req.setImageUrl(List.of("img.jpg"));

        PostDetail post = new PostDetail();
        post.setAuthority(1);

        when(postService.getPost(eq(1))).thenReturn(post);
        when(experienceService.getLevel(anyInt())).thenReturn(2);
        when(examineService.TextCensor(anyString())).thenReturn(null);
        when(commentService.addComment(any(CommentPostRequest.class), anyString())).thenReturn(true);
        when(ossService.submitFile(anyString())).thenReturn(true);
        when(experienceService.changeExp(eq("10000001"), anyInt())).thenReturn(true);
        when(postService.updateUpdateTime(eq(1))).thenReturn(true);

        mockMvc.perform(post("/post/postComment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(commentService).addComment(any(CommentPostRequest.class), anyString());

        req.setContent("");
        mockMvc.perform(post("/post/postComment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listComment() throws Exception {
        ListCommentRequest req = new ListCommentRequest();
        req.setPostId(1);
        Page<CommentEntry> page = new Page<>();
        when(commentService.getCommentList(any(ListCommentRequest.class))).thenReturn(page);

        mockMvc.perform(post("/post/commentList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(commentService).getCommentList(any(ListCommentRequest.class));

        when(commentService.getCommentList(any(ListCommentRequest.class))).thenThrow(new CommentException("Error"));
        mockMvc.perform(post("/post/commentList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void listReply() throws Exception {
        ListReplyRequest req = new ListReplyRequest();
        req.setCommentId(1);
        List<ReplyEntry> replies = List.of(new ReplyEntry());
        when(replyService.getReplyList(any(ListReplyRequest.class))).thenReturn(replies);

        mockMvc.perform(post("/post/replyList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(replyService).getReplyList(any(ListReplyRequest.class));

        when(replyService.getReplyList(any(ListReplyRequest.class))).thenThrow(new ReplyException("Error"));
        mockMvc.perform(post("/post/replyList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void replyComment() throws Exception {
        ReplyCommentRequest req = new ReplyCommentRequest();
        req.setPostId(1);
        req.setCommentId(1);
        req.setContent("Reply");

        PostDetail post = new PostDetail();
        post.setAuthority(1);

        when(postService.getPost(eq(1))).thenReturn(post);
        when(experienceService.getLevel(anyInt())).thenReturn(2);
        when(examineService.TextCensor(anyString())).thenReturn(null);
        when(replyService.addReply(any(ReplyCommentRequest.class))).thenReturn(new ReplyEntry());
        when(experienceService.changeExp(eq("10000001"), anyInt())).thenReturn(true);
        when(postService.updateUpdateTime(eq(1))).thenReturn(true);

        mockMvc.perform(post("/post/commentReply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(replyService).addReply(any(ReplyCommentRequest.class));

        req.setContent("");
        mockMvc.perform(post("/post/commentReply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteComment() throws Exception {
        DeleteCommentRequest req = new DeleteCommentRequest();
        req.setCommentId(1);

        CommentEntry comment = new CommentEntry();
        comment.setImageUrl("img.jpg");

        when(commentService.getComment(eq(1))).thenReturn(comment);
        when(commentService.deleteComment(any(DeleteCommentRequest.class), eq(10000001))).thenReturn(true);
        doNothing().when(ossService).deleteFile(anyString());
        when(experienceService.changeExp(eq("10000001"), anyInt())).thenReturn(true);

        mockMvc.perform(delete("/post/commentDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(commentService).deleteComment(any(DeleteCommentRequest.class), eq(10000001));

        when(commentService.deleteComment(any(DeleteCommentRequest.class), eq(10000001))).thenReturn(false);
        mockMvc.perform(delete("/post/commentDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReply() throws Exception {
        DeleteReplyRequest req = new DeleteReplyRequest();
        req.setReplyId(1);

        when(replyService.deleteReply(any(DeleteReplyRequest.class), eq(10000001))).thenReturn(true);
        when(experienceService.changeExp(eq("10000001"), anyInt())).thenReturn(true);

        mockMvc.perform(delete("/post/replyDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(replyService).deleteReply(any(DeleteReplyRequest.class), eq(10000001));

        when(replyService.deleteReply(any(DeleteReplyRequest.class), eq(10000001))).thenReturn(false);
        mockMvc.perform(delete("/post/replyDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPostByHot() throws Exception {
        PostSummary summary = new PostSummary();
        summary.setUserId(10000001);

        when(postService.getPostByHot(eq(10000001))).thenReturn(List.of(summary));
        when(experienceService.getLevel(anyInt())).thenReturn(2);
        when(experienceService.getLevelName(eq(2))).thenReturn("Level 2");
        when(userService.getUserInfo(anyString())).thenReturn(new UserDetail());
        when(userService.getProfilePhotoUrl(anyInt())).thenReturn("photo.jpg");

        mockMvc.perform(get("/post/getHotPost")
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(postService).getPostByHot(eq(10000001));

        when(postService.getPostByHot(eq(10000001))).thenReturn(List.of(summary));
        when(experienceService.getLevel(anyInt())).thenReturn(-1);
        mockMvc.perform(get("/post/getHotPost")
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUserPost() throws Exception {
        ListUserPostRequest req = new ListUserPostRequest();
        Page<PostSummary> page = new Page<>();
        when(postService.postsOfUser(any(ListUserPostRequest.class))).thenReturn(page);

        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            // valid token
            DecodedJWT ok = mock(DecodedJWT.class);
            mockedStatic.when(() -> JwtUtil.verifyToken("valid")).thenReturn(ok);
            mockedStatic.when(() -> JwtUtil.getUserId("valid")).thenReturn("10000001");

            mockMvc.perform(post("/post/getUserPost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))
                            .header("token", "valid"))
                    .andExpect(status().isOk());
            verify(postService).postsOfUser(any(ListUserPostRequest.class));

            // invalid token
            mockedStatic.when(() -> JwtUtil.verifyToken("invalid"))
                    .thenThrow(new RuntimeException("Invalid"));

            mockMvc.perform(post("/post/getUserPost")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(req))
                            .header("token", "invalid"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.message").value("Invalid or expired token"));
        }
    }
}
