package com.example.lal.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Date;

import com.example.lal.mapper.AdminMapper;
import com.example.lal.model.domain.Admin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.lal.mapper.UserMapper;
import com.example.lal.model.domain.User;
import com.example.lal.model.entity.UserDetail;
import com.example.lal.model.entity.UserSummary;
import com.example.lal.model.exceptions.UserException;
import com.example.lal.model.request.user.UpdateAccountInfoRequest;
import com.example.lal.model.request.user.VerifyRegisterRequest;
import com.example.lal.model.request.user.VerifyUserLoginRequest;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserMapper userMapper;

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private  UserServiceImpl userService;

    private VerifyRegisterRequest validRegisterRequest;
    private VerifyRegisterRequest invalidRegisterRequest;
    private VerifyUserLoginRequest validLoginRequest;
    private VerifyUserLoginRequest invalidLoginRequest;
    private UpdateAccountInfoRequest validUpdateRequest;
    private UpdateAccountInfoRequest invalidUpdateRequest;
    private User testUser;

    @BeforeEach
    void setUp(){
        validRegisterRequest = new VerifyRegisterRequest();
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("Password123");

        invalidRegisterRequest = new VerifyRegisterRequest();
        invalidRegisterRequest.setEmail("existing@example.com");
        invalidRegisterRequest.setPassword("Password123");

        validLoginRequest = new VerifyUserLoginRequest();
        validLoginRequest.setIdOrEmail("test@example.com");
        validLoginRequest.setPassword("Password123");

        invalidLoginRequest = new VerifyUserLoginRequest();
        invalidLoginRequest.setIdOrEmail("nonexistent@example.com");
        invalidLoginRequest.setPassword("WrongPassword");

        validUpdateRequest = new UpdateAccountInfoRequest();
        validUpdateRequest.setId(10000001);
        validUpdateRequest.setName("Updated User");
        validUpdateRequest.setEmail("updated@example.com");

        invalidUpdateRequest = new UpdateAccountInfoRequest();
        invalidUpdateRequest.setId(99999999);

        testUser = new User();
        testUser.setId(10000001);
        testUser.setEmail("test@example.com");
        testUser.setPassword("Password123");
        testUser.setRegisterTime(new Date(System.currentTimeMillis()));
    }

    @Test
    void addUser_success() {
        when(userMapper.getUserSummaryByEmail("test@example.com")).thenReturn(null);
        when(userMapper.addUser(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(10000001);
            return 1;
        });
        when(userMapper.setUserInitName("test@example.com")).thenReturn(true);

        try {
            String userId = userService.addUser(validRegisterRequest);
            assertNotNull(userId);
            assertEquals("10000001", userId);
            verify(userMapper, times(1)).getUserSummaryByEmail("test@example.com");
            verify(userMapper, times(1)).addUser(any(User.class));
            verify(userMapper, times(1)).setUserInitName("test@example.com");
        } catch (UserException e) {
            fail("Unexpected UserException: " + e.getMessage());
        }
    }

    @Test
    void addUser_emailExists() {
        UserSummary existingUser = new UserSummary();
        existingUser.setId(10000001);
        existingUser.setName("existingUser");

        when(userMapper.getUserSummaryByEmail("existing@example.com")).thenReturn(existingUser);
        assertThrows(UserException.class, () -> userService.addUser(invalidRegisterRequest));
        verify(userMapper, times(1)).getUserSummaryByEmail("existing@example.com");
        verify(userMapper, never()).addUser(any(User.class));
        verify(userMapper, never()).setUserInitName(anyString());
    }

    @Test
    void addAdmin_success() {
        when(adminMapper.getAdminByEmail("test@example.com")).thenReturn(null);
        when(adminMapper.addAdmin(any(Admin.class))).thenAnswer(invocation -> {
            Admin admin = invocation.getArgument(0);
            admin.setId(20000001);
            return true;
        });

        try {
            String adminId = userService.addAdmin(validRegisterRequest);
            assertNotNull(adminId);
            assertEquals("20000001", adminId);
            verify(adminMapper, times(1)).getAdminByEmail("test@example.com");
            verify(adminMapper, times(1)).addAdmin(any(Admin.class));
        } catch (UserException e) {
            fail("Unexpected UserException: " + e.getMessage());
        }
    }

    @Test
    void addAdmin_emailExists() {
        Admin existingAdmin = new Admin();
        existingAdmin.setId(20000001);
        existingAdmin.setEmail("existing@example.com");

        when(adminMapper.getAdminByEmail("existing@example.com")).thenReturn(existingAdmin);

        assertThrows(UserException.class, () -> userService.addAdmin(invalidRegisterRequest));
        verify(adminMapper, times(1)).getAdminByEmail("existing@example.com");
        verify(adminMapper, never()).addAdmin(any(Admin.class));
    }

    @Test
    void authenticateUser_success() {
        User user = new User();
        user.setId(10000001);
        user.setEmail("test@example.com");
        user.setPassword("Password123");
        user.setRegisterTime(new Date(System.currentTimeMillis()));

        when(userMapper.getUserByIdOrEmail("test@example.com", "Password123")).thenReturn(user);
        try {
            User result = userService.authenticateUser(validLoginRequest);
            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());
            verify(userMapper, times(1)).getUserByIdOrEmail("test@example.com", "Password123");
        } catch (UserException e) {
            fail("Unexpected UserException: " + e.getMessage());
        }
    }

    @Test
    void authenticateUser_invalidCredentials() {
        when(userMapper.getUserByIdOrEmail("nonexistent@example.com", "WrongPassword")).thenReturn(null);
        assertThrows(UserException.class, () -> userService.authenticateUser(invalidLoginRequest));
        verify(userMapper, times(1)).getUserByIdOrEmail("nonexistent@example.com", "WrongPassword");
    }

    @Test
    void getUserInfo_success() {
        UserDetail userDetail = new UserDetail();
        userDetail.setId(10000001);
        userDetail.setEmail("test@example.com");
        userDetail.setName("Test User");

        when(userMapper.getUserById("10000001")).thenReturn(userDetail);
        try {
            UserDetail result = userService.getUserInfo("10000001");
            assertNotNull(result);
            assertEquals("test@example.com", result.getEmail());
            assertEquals("Test User", result.getName());
            verify(userMapper, times(1)).getUserById("10000001");
        } catch (UserException e) {
            fail("Unexpected UserException: " + e.getMessage());
        }
    }

    @Test
    void getUserInfo_userNotFound() {
        when(userMapper.getUserById("99999999")).thenReturn(null);
        assertThrows(UserException.class, () -> userService.getUserInfo("99999999"));
        verify(userMapper, times(1)).getUserById("99999999");
    }

    @Test
    void updateAccountInfo_success() {
        UserDetail existingUser = new UserDetail();
        existingUser.setId(10000001);
        existingUser.setEmail("test@example.com");

        when(userMapper.getUserById("10000001")).thenReturn(existingUser);
        when(userMapper.updateUser(any(UpdateAccountInfoRequest.class))).thenReturn(true);
        boolean result = userService.updateAccountInfo(validUpdateRequest);
        assertTrue(result);
        verify(userMapper, times(1)).getUserById("10000001");
        verify(userMapper, times(1)).updateUser(validUpdateRequest);
    }

    @Test
    void updateAccountInfo_userNotFound() {
        when(userMapper.getUserById("99999999")).thenReturn(null);
        boolean result = userService.updateAccountInfo(invalidUpdateRequest);
        assertFalse(result);
        verify(userMapper, times(1)).getUserById("99999999");
        verify(userMapper, never()).updateUser(any(UpdateAccountInfoRequest.class));
    }


    @Test
    void addUserOnline_success() {
        String token = "valid-token";
        when(userMapper.addUserOnline(testUser)).thenReturn(1);
        when(userMapper.setUserOnlineToken(10000001, token)).thenReturn(1);
        try {
            userService.addUserOnline(testUser, token);
            verify(userMapper, times(1)).addUserOnline(testUser);
            verify(userMapper, times(1)).setUserOnlineToken(10000001, token);
        } catch (DataIntegrityViolationException e) {
            fail("Unexpected DataIntegrityViolationException: " + e.getMessage());
        }
    }

    @Test
    void addUserOnline_dataIntegrityViolation() {
        String token = "valid-token";
        when(userMapper.addUserOnline(testUser)).thenThrow(new DataIntegrityViolationException("Duplicate entry"));
        assertThrows(DataIntegrityViolationException.class, () -> userService.addUserOnline(testUser, token));
        verify(userMapper, times(1)).addUserOnline(testUser);
        verify(userMapper, never()).setUserOnlineToken(anyInt(), anyString());
    }

    @Test
    void deleteAccount_success() {
        when(userMapper.getUserById("10000001")).thenReturn(new UserDetail());
        when(userMapper.deleteUserById("10000001")).thenReturn(true);
        boolean result = userService.deleteAccount("10000001");
        assertTrue(result);
        verify(userMapper, times(1)).getUserById("10000001");
        verify(userMapper, times(1)).deleteUserById("10000001");
    }

    @Test
    void deleteAccount_userNotFound() {
        when(userMapper.getUserById("99999999")).thenReturn(null);
        boolean result = userService.deleteAccount("99999999");
        assertFalse(result);
        verify(userMapper, times(1)).getUserById("99999999");
        verify(userMapper, never()).deleteUserById(anyString());
    }
}