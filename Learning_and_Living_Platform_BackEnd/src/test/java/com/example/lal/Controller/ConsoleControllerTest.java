package com.example.lal.Controller;

import com.example.lal.model.domain.Admin;
import com.example.lal.model.request.user.VerifyRegisterRequest;
import com.example.lal.model.exceptions.UserException;
import com.example.lal.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Scanner;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ConsoleControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private ConsoleController consoleController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // 正向用例：成功注册管理员
    @Test
    void testVerifyAdminRegister_Success() throws UserException {
        VerifyRegisterRequest request = new VerifyRegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userService.addAdmin(request)).thenReturn("1");

        String result = consoleController.verifyAdminRegister(request);
        assertTrue(result.contains("注册成功"));
        assertTrue(result.contains("你的Id为：1"));
        verify(userService).addAdmin(request);
    }

    // 正向用例：成功重置密码
    @Test
    void testResetAdminPwd_Success() {
        String email = "admin@example.com";
        String newPassword = "newPass123";
        Admin admin = new Admin();
        admin.setEmail(email);

        when(userService.getAdminByEmail(email)).thenReturn(admin);

        String result = consoleController.resetAdminPwd(email, newPassword);

        assertEquals("修改成功", result);
        verify(userService).updateAdmin(admin);
        assertEquals(newPassword, admin.getPassword()); // 验证密码为明文
    }

    // 反向用例：邮箱已注册
    @Test
    void testVerifyAdminRegister_AlreadyRegistered() throws UserException {
        VerifyRegisterRequest request = new VerifyRegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");

        when(userService.addAdmin(request)).thenThrow(new UserException("该邮箱已被注册"));

        String result = consoleController.verifyAdminRegister(request);
        assertTrue(result.contains("注册失败"));
        assertTrue(result.contains("该邮箱已被注册"));
    }

    // 反向用例：管理员不存在
    @Test
    void testResetAdminPwd_AdminNotExist() {
        String email = "notexist@example.com";
        String newPassword = "newPass123";

        when(userService.getAdminByEmail(email)).thenReturn(null);

        String result = consoleController.resetAdminPwd(email, newPassword);
        assertEquals("该管理员不存在", result);
    }

    // 边界用例：空邮箱或密码
    @Test
    void testVerifyAdminRegister_EmptyEmailOrPassword() {
        VerifyRegisterRequest request = new VerifyRegisterRequest();
        request.setEmail("");
        request.setPassword("password123");

        String result = consoleController.verifyAdminRegister(request);
        assertEquals("注册失败！邮箱或密码不能为空", result);

        request.setEmail("test@example.com");
        request.setPassword("");
        result = consoleController.verifyAdminRegister(request);
        assertEquals("注册失败！邮箱或密码不能为空", result);
    }

    // 控制台命令测试：addAdmin 成功
    @Test
    void testProcessCommand_AddAdminSuccess() throws Exception {
        String input = "addAdmin test@example.com password123";
        when(userService.addAdmin(any(VerifyRegisterRequest.class))).thenReturn("1");

        String output = tapSystemOut(() -> consoleController.processCommand(input));
        assertTrue(output.contains("注册成功"));
    }

    // 控制台命令测试：addAdmin 参数错误
    @Test
    void testProcessCommand_AddAdminInvalidArgs() throws Exception {
        String input = "addAdmin test@example.com";

        String output = tapSystemOut(() -> consoleController.processCommand(input));
        assertTrue(output.contains("addAdmin需要两个参数，分别是邮箱和密码，你的参数数量错误！"));
    }

    // 控制台命令测试：--help
    @Test
    void testProcessCommand_HelpCommand() throws Exception {
        String input = "--help";

        String output = tapSystemOut(() -> consoleController.processCommand(input));
        assertTrue(output.contains("目前支持的指令有：addAdmin, resetAdminPwd"));
    }

    // 控制台命令测试：无效命令
    @Test
    void testProcessCommand_InvalidCommand() throws Exception {
        String input = "invalidCommand";

        String output = tapSystemOut(() -> consoleController.processCommand(input));
        assertTrue(output.contains("不支持此指令！输入 --help 查看指令"));
    }
}