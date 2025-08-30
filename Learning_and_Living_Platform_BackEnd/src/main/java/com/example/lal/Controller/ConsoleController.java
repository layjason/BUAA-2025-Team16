package com.example.lal.Controller;

import com.example.lal.model.domain.Admin;
import com.example.lal.model.exceptions.UserException;
import com.example.lal.model.request.user.VerifyRegisterRequest;
import com.example.lal.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Controller
public class ConsoleController {
    @Resource
    private UserService userService;

    public void startConsole(Scanner scanner) {
        System.out.println("请输入指令：");
        while (scanner.hasNextLine()) {
            String userInput = scanner.nextLine().trim();
            if (userInput.isEmpty()) {
                System.out.println("输入为空，请重新输入指令！");
                continue;
            }
            System.out.println("您输入的指令是：" + userInput);
            processCommand(userInput);
        }
    }

    protected void processCommand(String userInput) { // 改为 protected
        String[] argStr = userInput.split("\\s+");
        switch (argStr[0]) {
            case "addAdmin" -> {
                if (argStr.length != 3) {
                    System.out.println("addAdmin需要两个参数，分别是邮箱和密码，你的参数数量错误！");
                    return;
                }
                VerifyRegisterRequest req = new VerifyRegisterRequest();
                req.setEmail(argStr[1]);
                req.setPassword(argStr[2]);
                System.out.println(this.verifyAdminRegister(req));
            }
            case "resetAdminPwd" -> {
                if (argStr.length != 3) {
                    System.out.println("resetAdminPwd需要两个参数，分别是邮箱和密码，你的参数数量错误！");
                    return;
                }
                System.out.println(this.resetAdminPwd(argStr[1], argStr[2]));
            }
            case "--help" -> {
                System.out.println("目前支持的指令有：addAdmin, resetAdminPwd");
            }
            default -> System.out.println("不支持此指令！输入 --help 查看指令");
        }
    }

    public String verifyAdminRegister(VerifyRegisterRequest verifyRegisterRequest) {
        String email = verifyRegisterRequest.getEmail();
        String password = verifyRegisterRequest.getPassword();

        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            return "注册失败！邮箱或密码不能为空";
        }

        try {
            String adminId = userService.addAdmin(verifyRegisterRequest);
            return "注册成功！\n你的Id为：" + adminId;
        } catch (UserException e) {
            return "注册失败！原因：" + e.getMessage();
        }
    }

    public String resetAdminPwd(String email, String newPassword) {
        if (email == null || email.isEmpty() || newPassword == null || newPassword.isEmpty()) {
            return "修改失败！邮箱或密码不能为空";
        }

        Admin admin = userService.getAdminByEmail(email);
        if (admin == null) {
            return "该管理员不存在";
        }

        admin.setPassword(newPassword);
        userService.updateAdmin(admin);
        return "修改成功";
    }
}