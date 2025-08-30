package com.example.lal.Controller;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.lal.LalApplication;  // Import the main app class
import com.example.lal.constant.OtherConstants;
import com.example.lal.constant.UserConstants;
import com.example.lal.model.RestBean;
import com.example.lal.model.entity.*;
import com.example.lal.model.exceptions.CommentException;
import com.example.lal.model.exceptions.LikeException;
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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = LalApplication.class)  // Explicitly load the main config class
@AutoConfigureMockMvc  // Enables MockMvc with full context
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private LikeService likeService;

    @MockBean
    private OSSService ossService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private ReplyService replyService;

    @MockBean
    private UserService userService;

    @MockBean
    private ExperienceService experienceService;

    @MockBean
    private ExamineService examineService;


    @Test
    void uploadImage_positive_successfulUpload() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "content".getBytes());
        when(ossService.uploadFile(any(), eq("0"))).thenReturn("/path/test.jpg");

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

        verify(ossService).uploadFile(any(), eq("0"));
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

        verify(ossService, never()).uploadFile(any(), anyString());
    }

    @Test
    void uploadPost_positive_successfulUpload() throws Exception {
        AddPostRequest req = new AddPostRequest();
        req.setTitle("Title");
        req.setContent("Content");
        req.setAuthority(1);
        req.setImages(Arrays.asList("img1.jpg"));

        when(experienceService.getLevel(eq(10000001))).thenReturn(2);
        when(examineService.TextCensor(anyString())).thenReturn(null);
        when(ossService.submitFile(anyString())).thenReturn(true);
        when(postService.addPost(any(), anyString(), eq(10000001))).thenReturn(1);
        when(experienceService.changeExp(eq("10000001"), eq(5))).thenReturn(true);

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

        verify(postService).addPost(any(), anyString(), eq(10000001));
    }

    @Test
    void uploadPost_negative_noTitle() throws Exception {
        AddPostRequest req = new AddPostRequest();
        req.setTitle("");
        req.setContent("Content");
        req.setAuthority(1);
        req.setImages(Arrays.asList("img1.jpg"));

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
        // Positive case: Successful open post
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
        when(commentService.getCommentList(any())).thenReturn(comments);
        when(experienceService.changeExp(eq("10000001"), eq(1))).thenReturn(true);
        doNothing().when(postService).addBrowse(eq(1));

        mockMvc.perform(post("/post/postDetail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
        verify(postService, times(2)).getPost(eq(1));  // Called twice in controller

        // Negative case: Post not found
        when(postService.getPost(eq(1))).thenThrow(new PostException("Not found"));
        mockMvc.perform(post("/post/postDetail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void listPost() throws Exception {
        // Positive case: Successful list
        ListPostRequest req = new ListPostRequest();
        Page<PostSummary> page = new Page<>();
        PostSummary summary = new PostSummary();
        summary.setUserId(10000001);
        page.setList(Arrays.asList(summary));
        when(postService.getPostList(eq(10000001), any())).thenReturn(page);
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
        verify(postService).getPostList(eq(10000001), any());

        // Negative case: PostException
        when(postService.getPostList(eq(10000001), any())).thenThrow(new PostException("Error"));
        mockMvc.perform(post("/post/postList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deletePost() throws Exception {
        // Positive case: Successful deletion
        DeletePostRequest req = new DeletePostRequest();
        req.setPostId(1);
        PostDetail post = new PostDetail();
        post.setImageurls("img1.jpg");
        Page<CommentEntry> comments = new Page<>();
        CommentEntry comment = new CommentEntry();
        comment.setImageUrl("comment_img.jpg");
        comments.setList(Arrays.asList(comment));
        when(postService.getPost(eq(1))).thenReturn(post);
        when(commentService.getCommentList(any())).thenReturn(comments);
        when(postService.deletePost(any())).thenReturn(true);
        when(experienceService.changeExp(eq("10000001"), eq(-3))).thenReturn(true);
        doNothing().when(ossService).deleteFile(anyString());

        mockMvc.perform(delete("/post/postDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(postService).deletePost(any());

        // Negative case: Deletion fails
        when(postService.deletePost(any())).thenReturn(false);
        mockMvc.perform(delete("/post/postDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void likePost() throws Exception {
        // Positive case: Add like
        LikePostRequest req = new LikePostRequest();
        req.setPostId(1);
        req.setIsLike(false);
        PostDetail post = new PostDetail();
        post.setAuthority(1);
        post.setUserId(10000002);
        when(postService.getPost(eq(1))).thenReturn(post);
        when(experienceService.getLevel(eq(10000001))).thenReturn(2);
        when(likeService.addLike(eq(10000001), eq(1))).thenReturn(true);
        when(experienceService.changeExp(anyString(), eq(1))).thenReturn(true);

        mockMvc.perform(post("/post/postLike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(likeService).addLike(eq(10000001), eq(1));

        // Negative case: Insufficient authority
        when(experienceService.getLevel(eq(10000001))).thenReturn(0);
        mockMvc.perform(post("/post/postLike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void commentPost() throws Exception {
        // Positive case: Successful comment
        CommentPostRequest req = new CommentPostRequest();
        req.setPostId(1);
        req.setContent("Comment");
        req.setImageUrl(Arrays.asList("img.jpg"));
        PostDetail post = new PostDetail();
        post.setAuthority(1);
        when(postService.getPost(eq(1))).thenReturn(post);
        when(experienceService.getLevel(eq(10000001))).thenReturn(2);
        when(examineService.TextCensor(anyString())).thenReturn(null);
        when(commentService.addComment(any(), anyString())).thenReturn(true);
        when(ossService.submitFile(anyString())).thenReturn(true);
        when(experienceService.changeExp(eq("10000001"), eq(3))).thenReturn(true);
        when(postService.updateUpdateTime(eq(1))).thenReturn(true);

        mockMvc.perform(post("/post/postComment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(commentService).addComment(any(), anyString());

        // Negative case: No content
        req.setContent("");
        mockMvc.perform(post("/post/postComment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listComment() throws Exception {
        // Positive case: Successful list
        ListCommentRequest req = new ListCommentRequest();
        req.setPostId(1);
        Page<CommentEntry> page = new Page<>();
        when(commentService.getCommentList(any())).thenReturn(page);

        mockMvc.perform(post("/post/commentList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(commentService).getCommentList(any());

        // Negative case: CommentException
        when(commentService.getCommentList(any())).thenThrow(new CommentException("Error"));
        mockMvc.perform(post("/post/commentList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void listReply() throws Exception {
        // Positive case: Successful list
        ListReplyRequest req = new ListReplyRequest();
        req.setCommentId(1);
        List<ReplyEntry> replies = Arrays.asList(new ReplyEntry());
        when(replyService.getReplyList(any())).thenReturn(replies);

        mockMvc.perform(post("/post/replyList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(replyService).getReplyList(any());

        // Negative case: ReplyException
        when(replyService.getReplyList(any())).thenThrow(new ReplyException("Error"));
        mockMvc.perform(post("/post/replyList")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void replyComment() throws Exception {
        // Positive case: Successful reply
        ReplyCommentRequest req = new ReplyCommentRequest();
        req.setPostId(1);
        req.setCommentId(1);
        req.setContent("Reply");
        PostDetail post = new PostDetail();
        post.setAuthority(1);
        when(postService.getPost(eq(1))).thenReturn(post);
        when(experienceService.getLevel(eq(10000001))).thenReturn(2);
        when(examineService.TextCensor(anyString())).thenReturn(null);
        when(replyService.addReply(any())).thenReturn(new ReplyEntry());
        when(experienceService.changeExp(eq("10000001"), eq(1))).thenReturn(true);
        when(postService.updateUpdateTime(eq(1))).thenReturn(true);

        mockMvc.perform(post("/post/commentReply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(replyService).addReply(any());

        // Negative case: No content
        req.setContent("");
        mockMvc.perform(post("/post/commentReply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteComment() throws Exception {
        // Positive case: Successful deletion
        DeleteCommentRequest req = new DeleteCommentRequest();
        req.setCommentId(1);
        CommentEntry comment = new CommentEntry();
        comment.setImageUrl("img.jpg");
        when(commentService.getComment(eq(1))).thenReturn(comment);
        when(commentService.deleteComment(any(), eq(10000001))).thenReturn(true);
        doNothing().when(ossService).deleteFile(anyString());
        when(experienceService.changeExp(eq("10000001"), eq(-2))).thenReturn(true);

        mockMvc.perform(delete("/post/commentDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(commentService).deleteComment(any(), eq(10000001));

        // Negative case: Deletion fails
        when(commentService.deleteComment(any(), eq(10000001))).thenReturn(false);
        mockMvc.perform(delete("/post/commentDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteReply() throws Exception {
        // Positive case: Successful deletion
        DeleteReplyRequest req = new DeleteReplyRequest();
        req.setReplyId(1);
        when(replyService.deleteReply(any(), eq(10000001))).thenReturn(true);
        when(experienceService.changeExp(eq("10000001"), eq(-1))).thenReturn(true);

        mockMvc.perform(delete("/post/replyDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(replyService).deleteReply(any(), eq(10000001));

        // Negative case: Deletion fails
        when(replyService.deleteReply(any(), eq(10000001))).thenReturn(false);
        mockMvc.perform(delete("/post/replyDelete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPostByHot() throws Exception {
        // Positive case: Successful hot posts
        PostSummary summary = new PostSummary();
        summary.setUserId(10000001);
        when(postService.getPostByHot(eq(10000001))).thenReturn(Arrays.asList(summary));
        when(experienceService.getLevel(anyInt())).thenReturn(2);
        when(experienceService.getLevelName(eq(2))).thenReturn("Level 2");
        when(userService.getUserInfo(anyString())).thenReturn(new UserDetail());
        when(userService.getProfilePhotoUrl(anyInt())).thenReturn("photo.jpg");

        mockMvc.perform(get("/post/getHotPost")
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isOk());
        verify(postService).getPostByHot(eq(10000001));

        // Negative case: Level fetch error
        when(postService.getPostByHot(eq(10000001))).thenReturn(Arrays.asList(summary));
        when(experienceService.getLevel(anyInt())).thenReturn(-1);
        mockMvc.perform(get("/post/getHotPost")
                        .requestAttr("userId", "10000001"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getUserPost() throws Exception {
        // Positive case: Successful user posts
        ListUserPostRequest req = new ListUserPostRequest();
        Page<PostSummary> page = new Page<>();
        when(JwtUtil.verifyToken(eq("valid"))).thenReturn(mock(DecodedJWT.class));
        when(JwtUtil.getUserId(eq("valid"))).thenReturn("10000001");
        when(postService.postsOfUser(any())).thenReturn(page);

        mockMvc.perform(post("/post/getUserPost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header("token", "valid"))
                .andExpect(status().isOk());
        verify(postService).postsOfUser(any());

        // Negative case: Invalid token
        when(JwtUtil.verifyToken(eq("invalid"))).thenThrow(new RuntimeException("Invalid"));
        mockMvc.perform(post("/post/getUserPost")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .header("token", "invalid"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid or expired token"));
    }
}