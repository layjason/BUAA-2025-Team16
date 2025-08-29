package com.example.lal.Controller;

import com.example.lal.mapper.UserMapper;
import com.example.lal.model.RestBean;
import com.example.lal.model.domain.Admin;
import com.example.lal.model.exceptions.UserException;
import com.example.lal.model.request.user.VerifyRegisterRequest;
import com.example.lal.service.UserService;
import com.example.lal.service.impl.UserServiceImpl;
import jakarta.annotation.Resource;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Controller;

import java.util.Scanner;
import java.util.UUID;
@Controller
public class ConsoleController {
    @Resource
    private UserService userService;

    public void startConsole() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入指令：");
        String userInput = scanner.nextLine();
        String[] argStr;
        while(true){
            System.out.println("您输入的指令是：" + userInput);
            argStr=userInput.trim().split(" +");
            //System.out.println(argStr[0]);
            switch (argStr[0]) {
                case "addAdmin" -> {
                    if (argStr.length != 3) {
                        System.out.println("addAdmin需要两个参数，分别是邮箱和密码，你的参数数量错误！");
                        break;
                    }
                    String  email = argStr[1];
                    String password = argStr[2];
                    if (!email.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
                        System.out.println("邮箱格式错误！");
                        break;
                    }
                    VerifyRegisterRequest verifyRegisterRequest = new VerifyRegisterRequest();
                    verifyRegisterRequest.setEmail(email);
                    verifyRegisterRequest.setPassword(password);
                    System.out.println(this.verifyAdminRegister(verifyRegisterRequest));
                }
                //userMapper.addAdmin()
                case "resetAdminPwd" -> {
                    if (argStr.length != 3) {
                        System.out.println("addAdmin需要两个参数，分别是邮箱和密码，你的参数数量错误！");
                        break;
                    }
                    String email = argStr[1];
                    String newPassword = argStr[2];
                    if (!email.matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
                        System.out.println("邮箱格式错误！");
                        break;
                    }
                    System.out.println(this.resetAdminPwd(email, newPassword));
                }
                case "--help" -> {
                    System.out.println("目前支持的指令有：");
                    System.out.println("addAdmin [email] [password]:添加一名管理员;");
                    System.out.println("resetAdminPwd [email] [newPassword]:重置邮箱为[email]的管理员的密码为[newPassword].");
                }
                default -> System.out.println("不支持此指令！\n您可以通过输入指令“--help”查看支持的指令~");
            }
            userInput = scanner.nextLine();
        }
        // 在这里对用户输入进行处理
    }
    public String verifyAdminRegister(VerifyRegisterRequest verifyRegisterRequest){
        System.out.println(verifyRegisterRequest.getEmail());
        String password = verifyRegisterRequest.getPassword();

        verifyRegisterRequest.setPassword(password);
        try{
            String adminId = userService.addAdmin(verifyRegisterRequest);
            return "注册成功！\n你的Id为："+adminId;
        } catch (UserException e) {
            return "注册失败！";
        }
    }
    public String resetAdminPwd(String email,String newPassword){
        Admin admin = userService.getAdminByEmail(email);
        if(admin == null) return "该管理员不存在";
        //进行两次加盐加密，分别模拟前端和后端的加盐加密
        newPassword = newPassword;

        admin.setPassword(newPassword);
        userService.updateAdmin(admin);
        return "修改成功";
    }
}
