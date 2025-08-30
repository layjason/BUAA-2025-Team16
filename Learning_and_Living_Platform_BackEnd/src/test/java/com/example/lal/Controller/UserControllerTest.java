package com.example.lal.Controller;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.lal.constant.ExpConstants;
import com.example.lal.model.domain.User;
import com.example.lal.model.entity.LevelEntry;
import com.example.lal.model.entity.Page;
import com.example.lal.model.entity.UserDetail;
import com.example.lal.model.entity.UserOnline;
import com.example.lal.model.exceptions.UserException;
import com.example.lal.model.request.user.*;
import com.example.lal.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private  UserController userController;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @MockBean
    private LogService logService;

    @MockBean
    private ExperienceService experienceService;

    @MockBean
    MailService mailService;

    private User mockUser;
    private User mockAdmin;
    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(10000001);
        mockUser.setName("testUser");
        mockUser.setPassword("encryptedPassword");
        mockUser.setEmail("user@example.com");
        mockUser.setLogInNum(1);

        mockAdmin = new User();
        mockAdmin.setId(9999999);
        mockAdmin.setName("testAdmin");
        mockAdmin.setPassword("encryptedPassword");
        mockAdmin.setEmail("admin@example.com");
    }

    @Test
    void verifyTokenAdmin_success() throws Exception{
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);

            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("9999999"); // 管理员 ID
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("9999999");

            mockMvc.perform(get("/user/verifyToken")
                            .header("token", "mock-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value(1)); // 管理员返回 1
        }
    }


    @Test
    void verifyTokenUser_success() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);

            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("10000001"); // 普通用户 ID
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            mockMvc.perform(get("/user/verifyToken")
                            .header("token", "mock-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value(0)); // 普通用户返回 0
        }
    }

    @Test
    void verifyToken_invalidToken () throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.verifyToken("invalid-token"))
                    .thenThrow(new com.auth0.jwt.exceptions.JWTDecodeException("The token was expected to have 3 parts, but got 1"));

            mockMvc.perform(get("/user/verifyToken")
                            .header("token", "invalid-token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("Invalid or expired token"));
        }
    }

    @Test
    void verifyToken_missingToken() throws Exception {
        mockMvc.perform(get("/user/verifyToken"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.message").value("Token is null"));
    }

    @Test
    void verifyAdminLogin_success() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            VerifyAdminLoginRequest request = new VerifyAdminLoginRequest();
            request.setAccount("admin@example.com");
            request.setPassword("encryptedPassword");
            String mockToken = "mock-admin-token";

            when(userService.authenticateAdmin(any(VerifyAdminLoginRequest.class))).thenReturn(mockAdmin);
            doNothing().when(userService).addUserOnline(eq(mockAdmin), eq(mockToken));
            mockedStatic.when(() -> JwtUtil.createToken(mockAdmin)).thenReturn(mockToken);

            mockMvc.perform(post("/user/adminLogin")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message.id").value(mockAdmin.getId()))
                    .andExpect(jsonPath("$.message.token").value(mockToken));

            verify(userService).authenticateAdmin(any(VerifyAdminLoginRequest.class));
            verify(userService).addUserOnline(eq(mockAdmin), eq(mockToken));
        }
    }

    @Test
    void verifyAdminLogin_failure() throws Exception {
        VerifyAdminLoginRequest request = new VerifyAdminLoginRequest();
        request.setAccount("invalid@example.com");
        request.setPassword("wrongPassword");

        when(userService.authenticateAdmin(any(VerifyAdminLoginRequest.class)))
                .thenThrow(new UserException("用户不存在或密码错误"));

        mockMvc.perform(post("/user/adminLogin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("用户不存在或密码错误2"));
    }

    @Test
    void getLevel_success() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            when(experienceService.getLevel(10000001)).thenReturn(5);

            mockMvc.perform(get("/user/getLevel")
                            .header("token", "mock-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message.userLevel").value(5));
        }
    }

    @Test
    void getLevel_invalidToken() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.verifyToken("invalid-token"))
                    .thenThrow(new JWTDecodeException("Invalid token"));

            mockMvc.perform(get("/user/getLevel")
                            .header("token", "invalid-token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("Invalid or expired token"));
        }
    }

    @Test
    void verifyUserLogin_success() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            VerifyUserLoginRequest request = new VerifyUserLoginRequest();
            request.setIdOrEmail("user@example.com");
            request.setPassword("encryptedPassword");
            String mockToken = "mock-user-token";

            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");
            mockedStatic.when(() -> JwtUtil.createToken(mockUser)).thenReturn(mockToken);

            when(userService.authenticateUser(any(VerifyUserLoginRequest.class))).thenReturn(mockUser);
            doNothing().when(userService).addUserOnline(eq(mockUser), eq(mockToken));
            when(logService.getLastLog(eq("10000001"))).thenReturn(null); // 新的一天登录
            when(logService.addLog(eq("10000001"))).thenReturn(true);
            when(experienceService.changeExp(eq("10000001"), eq(ExpConstants.EXP_LOGIN))).thenReturn(true); // 修复：假设返回 boolean
            doNothing().when(userService).setLoginNum(eq("10000001"), eq(2));

            mockMvc.perform(post("/user/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message.id").value(mockUser.getId()))
                    .andExpect(jsonPath("$.message.token").value(mockToken));

            verify(userService).setLoginNum(eq("10000001"), eq(2));
            verify(logService).addLog(eq("10000001"));
            verify(experienceService).changeExp(eq("10000001"), eq(ExpConstants.EXP_LOGIN));
        }
    }

    @Test
    void verifyUserLogin_failure() throws Exception {
        VerifyUserLoginRequest request = new VerifyUserLoginRequest();
        request.setIdOrEmail("invalid@example.com");
        request.setPassword("wrongPassword");

        when(userService.authenticateUser(any(VerifyUserLoginRequest.class)))
                .thenThrow(new UserException("用户不存在或密码错误"));

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").isEmpty());
    }

    @Test
    void logout_success() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            doNothing().when(userService).deleteUserOnline(eq("10000001"));
            doNothing().when(logService).userLogout(eq(10000001));

            mockMvc.perform(get("/user/logout")
                            .header("token", "mock-token"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("logout success"));
        }
    }

    @Test
    void logout_invalidToken() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            mockedStatic.when(() -> JwtUtil.verifyToken("invalid-token"))
                    .thenThrow(new JWTDecodeException("Invalid token"));

            mockMvc.perform(get("/user/logout")
                            .header("token", "invalid-token"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("Invalid or expired token"));
        }
    }

    @Test
    void verifyUserRegister_success() throws Exception {
        VerifyRegisterRequest request = new VerifyRegisterRequest();
        request.setEmail("newuser@example.com");
        request.setPassword("newPassword");

        when(userService.addUser(any(VerifyRegisterRequest.class))).thenReturn("注册成功");

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("注册成功"));
    }

    @Test
    void verifyUserRegister_userExists() throws Exception {
        VerifyRegisterRequest request = new VerifyRegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("newPassword");

        when(userService.addUser(any(VerifyRegisterRequest.class)))
                .thenThrow(new UserException("用户已存在"));

        mockMvc.perform(post("/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("用户已存在"));
    }

    @Test
    void updateAccountInfo_success() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            UpdateAccountInfoRequest request = new UpdateAccountInfoRequest();
            request.setId(10000001);
            request.setName("updatedName");

            when(userService.updateAccountInfo(any(UpdateAccountInfoRequest.class))).thenReturn(true);

            mockMvc.perform(put("/user/updateUserInfo")
                            .header("token", "mock-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("修改成功"));
        }
    }

    @Test
    void updateAccountInfo_unauthorized() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            UpdateAccountInfoRequest request = new UpdateAccountInfoRequest();
            request.setId(10000002);

            mockMvc.perform(put("/user/updateUserInfo")
                            .header("token", "mock-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(403))
                    .andExpect(jsonPath("$.message").value("无权限修改"));
        }
    }

    @Test
    void getAccountInfoList_success() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("9999999");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("9999999");

            GetAccountInfoListRequest request = new GetAccountInfoListRequest();
            Page<UserDetail> mockPage = new Page<>();
            UserDetail userDetail = new UserDetail();
            userDetail.setId(10000001);
            mockPage.setList(Collections.singletonList(userDetail));
            when(userService.getUserDetailList(any(GetAccountInfoListRequest.class))).thenReturn(mockPage);
            when(experienceService.readExp(eq(10000001))).thenReturn(100);
            when(experienceService.getLevel(eq(10000001))).thenReturn(5);
            when(experienceService.getLevelName(eq(5))).thenReturn("Level 5");

            mockMvc.perform(post("/user/userListByPage")
                            .header("token", "mock-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message.list[0].id").value(10000001))
                    .andExpect(jsonPath("$.message.list[0].exp").value(100))
                    .andExpect(jsonPath("$.message.list[0].userLevel").value(5));
        }
    }

    @Test
    void getAccountInfoList_insufficientPermission() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            GetAccountInfoListRequest request = new GetAccountInfoListRequest();

            mockMvc.perform(post("/user/userListByPage")
                            .header("token", "mock-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(400))
                    .andExpect(jsonPath("$.message").value("权限等级不足"));
        }
    }
    @Test
    void getPassword_success() throws Exception {
        GetPasswordRequest request = new GetPasswordRequest();
        request.setEmail("user@example.com");

        when(userService.findUserByEmail(eq("user@example.com"))).thenReturn(mockUser);
        doNothing().when(userService).resetPassword(eq("user@example.com"), anyString());
        doNothing().when(mailService).sendSimpleMail(
                eq("YiYan_LAL@163.com"),
                eq("user@example.com"),
                isNull(),
                eq("【易言学习生活平台】找回密码"),
                anyString()
        );

        mockMvc.perform(post("/user/getPwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("密码已重置并发送到你的邮箱"));
    }

    @Test
    void getPassword_invalidEmail() throws Exception {
        GetPasswordRequest request = new GetPasswordRequest();
        request.setEmail("invalid-email"); // 无效邮箱格式

        when(userService.findUserByEmail(eq("invalid-email"))).thenReturn(null);

        mockMvc.perform(post("/user/getPwd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("用户不存在"));
    }

    @Test
    void updateUserPassword_success() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            UpdatePasswordRequest request = new UpdatePasswordRequest();
            request.setOldPassword("encryptedPassword");
            request.setNewPassword("newPassword");

            VerifyUserLoginRequest verifyRequest = new VerifyUserLoginRequest();
            verifyRequest.setIdOrEmail("10000001");
            verifyRequest.setPassword("encryptedPassword");
            when(userService.authenticateUser(any(VerifyUserLoginRequest.class))).thenReturn(mockUser);
            doNothing().when(userService).updatePassword(eq("10000001"), eq("newPassword"));

            mockMvc.perform(put("/user/updatePwd")
                            .header("token", "mock-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("修改成功"));
        }
    }

    @Test
    void updateAdminPassword_success() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("9999999");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("9999999");

            UpdatePasswordRequest request = new UpdatePasswordRequest();
            request.setUserId("10000001");
            request.setNewPassword("newPassword");

            doNothing().when(userService).updatePassword(eq("10000001"), eq("newPassword"));

            mockMvc.perform(put("/user/updatePwd")
                            .header("token", "mock-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("修改成功"));
        }
    }

    @Test
    void updatePassword_wrongOldPassword() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            UpdatePasswordRequest request = new UpdatePasswordRequest();
            request.setOldPassword("wrongPassword");
            request.setNewPassword("newPassword");

            VerifyUserLoginRequest verifyRequest = new VerifyUserLoginRequest();
            verifyRequest.setIdOrEmail("10000001");
            verifyRequest.setPassword("wrongPassword");
            when(userService.authenticateUser(any(VerifyUserLoginRequest.class)))
                    .thenThrow(new UserException("原密码错误"));

            mockMvc.perform(put("/user/updatePwd")
                            .header("token", "mock-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(401))
                    .andExpect(jsonPath("$.message").value("原密码错误"));
        }
    }

    @Test
    void deleteAccount_success() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            DeleteAccountRequest request = new DeleteAccountRequest();
            request.setId("10000001");

            when(userService.deleteAccount(eq("10000001"))).thenReturn(true);

            mockMvc.perform(delete("/user/deleteUser")
                            .header("token", "mock-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.success").value(true))
                    .andExpect(jsonPath("$.status").value(200))
                    .andExpect(jsonPath("$.message").value("删除成功"));
        }
    }

    @Test
    void deleteAccount_userNotFound() throws Exception {
        try (MockedStatic<JwtUtil> mockedStatic = mockStatic(JwtUtil.class)) {
            DecodedJWT mockedJWT = mock(DecodedJWT.class);
            Claim mockedClaim = mock(Claim.class);
            when(mockedJWT.getClaim("id")).thenReturn(mockedClaim);
            when(mockedClaim.asString()).thenReturn("10000001");
            when(mockedJWT.getIssuer()).thenReturn("yourExpectedIssuer");
            when(mockedJWT.getAudience()).thenReturn(List.of("yourExpectedAudience"));
            when(mockedJWT.getExpiresAt()).thenReturn(new Date(System.currentTimeMillis() + 3600000));
            when(mockedJWT.getIssuedAt()).thenReturn(new Date(System.currentTimeMillis() - 3600000));
            mockedStatic.when(() -> JwtUtil.verifyToken("mock-token")).thenReturn(mockedJWT);
            mockedStatic.when(() -> JwtUtil.getUserId("mock-token")).thenReturn("10000001");

            DeleteAccountRequest request = new DeleteAccountRequest();
            request.setId("99999999");

            when(userService.deleteAccount(eq("99999999"))).thenReturn(false);

            mockMvc.perform(delete("/user/deleteUser")
                            .header("token", "mock-token")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.success").value(false))
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").value("不存在此用户"));
        }
    }
}