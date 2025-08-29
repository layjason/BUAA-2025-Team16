package com.example.lal.Controller;


import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.lal.model.RestBean;
import com.example.lal.model.domain.Log;
import com.example.lal.model.domain.User;
import com.example.lal.model.entity.*;
import com.example.lal.model.exceptions.UserException;
import com.example.lal.model.request.user.*;
import com.example.lal.service.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.example.lal.constant.ExpConstants.*;
import static com.example.lal.constant.OtherConstants.HEARTBEAT_TIME;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private LogService logService;
    @Resource
    private ExperienceService expService;
    @Resource
    private OSSService ossService;

    @Resource
    private ExamineService examineService;

    @Autowired
    private MailService mailService;


    @GetMapping("/hello")//该接口仅用于测试后端服务是否启动
    public ResponseEntity<RestBean> hello(){
        System.out.println("Hello!!!");
        List<UserOnline> allUserOnline = userService.getAllUserOnline();
        for(UserOnline userOnline:allUserOnline){
            System.out.println(userOnline.getId());
        }
        /*mailService.sendSimpleMail("1415596561@qq.com",
                "20374151@buaa.edu.cn",
                null,
                "我是邮件的主题",
                "我是邮件的内容");*/
        //responseEntity= ResponseEntity.badRequest().body("Hello!!!");
        return RestBean.failure(400,"Hello!!!");
        //return RestBean.generate2("hello");
    }
    @GetMapping("/verifyToken")
    public ResponseEntity<RestBean> verifyToken(HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");
        int id = Integer.parseInt(userId);
        if(id<10000000) return RestBean.success(1);
        else return RestBean.success(0);
    }
    @PostMapping("/adminLogin")
    public ResponseEntity<RestBean> verifyAdminLogin(@RequestBody VerifyAdminLoginRequest verifyAdminLoginRequest, HttpServletRequest request){
        //检查用户登入信息，调用verifyUserLoginService, addLog
        System.out.println("adminLogin 调用成功");
        String password = verifyAdminLoginRequest.getPassword();
        password = saltEncryption(password,userService.getAdminSalt(verifyAdminLoginRequest.getAccount()));
        //password = saltEncryption(password,userService.getAdminSalt(verifyAdminLoginRequest.getAccount()));
        System.out.println("此用户的password(加密后)是:"+password);
        verifyAdminLoginRequest.setPassword(password);
        try{
            User adminLogin = userService.authenticateAdmin(verifyAdminLoginRequest);
            //logService.addLog(userDisplay.getId());
            String token =JwtUtil.createToken(adminLogin);
            try{
                userService.addUserOnline(adminLogin,token);
            }
            catch (DataIntegrityViolationException ex){
                if(ex instanceof DuplicateKeyException){
                    userService.deleteUserOnline(String.valueOf(adminLogin.getId()));
                    //logService.userLogout(adminLogin.getId());
                    userService.addUserOnline(adminLogin,token);
                }
            }
            //adminLogin.setPassword(null);
            return RestBean.success(new UserDisplay(adminLogin,token));
        }
        catch (UserException e){
            System.out.println("adminLogin 调用失败");
            //throw new UserException("用户不存在或密码错误");
            return RestBean.failure(400,"用户不存在或密码错误2");
        }
    }
    @PostMapping("/getSalt")
    public ResponseEntity<RestBean> getSalt(@RequestBody GetSaltRequest getSaltRequest, HttpServletRequest request){
        //检查用户登入信息，调用verifyUserLoginService, addLog, changeExp(成功登入加积分)
        String idOrEmail = getSaltRequest.getIdOrEmail();
        System.out.println("getSalt方法调用成功：传进来的idOrEmail是:"+idOrEmail);
        String userSalt = userService.getUserSalt(idOrEmail);
        String adminSalt = userService.getAdminSalt(idOrEmail);
        //随机生成一个字符串作为盐值，限制长度<=20
        String randomString = UUID.randomUUID().toString();
        randomString = randomString.replace("-", ""); // 去除字符串中的横线
        if(randomString.length()>20)randomString = randomString.substring(0,20);
        if(userSalt == null&&adminSalt == null) return RestBean.success(randomString);
        return RestBean.success(Objects.requireNonNullElse(userSalt, adminSalt));
    }

    @GetMapping("/getLevel")
    public ResponseEntity<RestBean> getLevel( HttpServletRequest request){
        int userId = Integer.parseInt((String) request.getAttribute("userId"));
        System.out.println("getLevel 调用成功");

        int level = expService.getLevel(userId);
        if(level < 0){
            return RestBean.failure(HttpStatus.BAD_REQUEST, "权限不足");
        }
        LevelEntry levelEntry = new LevelEntry();
        levelEntry.setUserLevel(level);
        return RestBean.success(levelEntry);
    }

    @PostMapping("/login")
    public ResponseEntity<RestBean> verifyUserLogin(@RequestBody VerifyUserLoginRequest verifyUserLoginRequest,
                                                    HttpServletRequest request){
        //检查用户登入信息，调用verifyUserLoginService, addLog, changeExp(成功登入加积分)
        System.out.println("login 调用成功:");
        try{
            String password = verifyUserLoginRequest.getPassword();
            User userLogin = userService.authenticateUser(verifyUserLoginRequest);
            String token =JwtUtil.createToken(userLogin);
            try{
                userService.addUserOnline(userLogin,token);
            }
            catch (DataIntegrityViolationException ex){
                if(ex instanceof DuplicateKeyException){
                    userService.deleteUserOnline(String.valueOf(userLogin.getId()));
                    logService.userLogout(userLogin.getId());
                    userService.addUserOnline(userLogin,token);
                }
            }
            Log lastLog = logService.getLastLog(String.valueOf(userLogin.getId()));
            Date currentDate = new Date();
            if(lastLog == null||!isSameDay(lastLog.getLoginTime(),currentDate)){
               userLogin.setLogInNum(userLogin.getLogInNum()+1);
            }
            System.out.println("此用户的LogInNum是:"+userLogin.getLogInNum());
            userService.setLoginNum(String.valueOf(userLogin.getId()),userLogin.getLogInNum());
            logService.addLog(String.valueOf(userLogin.getId()));
            expService.changeExp(String.valueOf(userLogin.getId()), EXP_LOGIN);

            return RestBean.success(new UserDisplay(userLogin,token));
        }
        catch (UserException e){
            System.out.println("login失败:用户不存在或密码错误");
            //throw new UserException("用户不存在或密码错误");
            return RestBean.failure(400,null);
        }
    }
    @GetMapping("/logout")
    public ResponseEntity<RestBean> logout(HttpServletRequest request){
        System.out.println("logout 调用成功");
        String id = (String) request.getAttribute("userId");
        userService.deleteUserOnline(id);
        if(Integer.parseInt(id)>10000000)
            logService.userLogout(Integer.parseInt(id));
        return RestBean.success("logout success");
    }
    private static boolean isSameDay(Date givenDate, Date currentDate) {
        boolean isSameDay = false;
        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(givenDate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(currentDate);

        if (calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.MONTH) == calendar2.get(Calendar.MONTH) &&
                calendar1.get(Calendar.DAY_OF_MONTH) == calendar2.get(Calendar.DAY_OF_MONTH)) {
            isSameDay = true;
        }
        return isSameDay;
    }
    @GetMapping("/heartbeat")
    public ResponseEntity<RestBean> handleHeartbeat(HttpServletRequest request){
        String token = request.getHeader("token");
        DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
        // 获取过期时间
        Date expirationTime = decodedJWT.getExpiresAt();
        // 获取当前时间
        Date currentTime = new Date();
        // 计算剩余有效时间（以毫秒为单位）
        long remainingTime = expirationTime.getTime() - currentTime.getTime();
        System.out.println("收到心跳 当前时间:"+currentTime+" 剩余有效时间:"+remainingTime);
        if(remainingTime<HEARTBEAT_TIME*2){
            User user = new User();
            user.setId(Integer.parseInt(userService.getUserIdByToken(token)));
            token = JwtUtil.createToken(user);
            userService.setUserOnlineToken(user.getId(),token);
        }
        return RestBean.success(token);
    }
    @PostMapping("/register")
    public ResponseEntity<RestBean> verifyUserRegister(@RequestBody VerifyRegisterRequest verifyRegisterRequest, HttpServletRequest request){
        System.out.println("register 调用成功");
        try{
            String password = verifyRegisterRequest.getPassword();
            String salt = verifyRegisterRequest.getSalt();
            password = saltEncryption(password,salt);
            verifyRegisterRequest.setPassword(password);
            String userId = userService.addUser(verifyRegisterRequest);
            return RestBean.success("注册成功");
        }
        catch (UserException e){
            //System.out.println("register失败:用户不存在或密码错误");
            //throw new UserException("用户不存在或密码错误");
            return RestBean.failure(400,e.getMessage());
        }
    }
    @Scheduled(fixedRate = 60000) // 每1分钟执行一次
    public void checkTokenExpiration() {
        System.out.println("检查在线用户表中token过期的任务开始执行:");
        List<UserOnline> allUserOnline = userService.getAllUserOnline();
        //System.out.println(allUserOnline);
        if(allUserOnline == null)return;
        for(UserOnline userOnline:allUserOnline){
            String token = userOnline.getToken();
            try {
                DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
                // 获取过期时间
                Date expirationTime = decodedJWT.getExpiresAt();
                // 获取当前时间
                System.out.println(userOnline.getId() + "的Token 过期时间: " + expirationTime);
            }
            catch (TokenExpiredException e){
                userService.deleteUserOnline(String.valueOf(userOnline.getId()));
                if(userOnline.getId()>10000000)
                    logService.userLogout(userOnline.getId());
            }
        }

    }
    @GetMapping("/userInfo")
    public ResponseEntity<RestBean> getUserInfo(HttpServletRequest request){
        System.out.println("getUserInfo 调用成功");
        try{
            String userId = (String) request.getAttribute("userId");
            //System.out.println("userId:"+userId);
            UserDetail userDetail = userService.getUserInfo(userId);
            int exp = expService.readExp(Integer.parseInt(userId));
            if(exp < 0){
                return RestBean.failure(HttpStatus.NOT_FOUND, "没有此用户的经验信息");
            }
            userDetail.setExp(exp);

            int level = expService.getLevel(Integer.parseInt(userId));
            if(level < 0){
                return RestBean.failure(HttpStatus.NOT_FOUND, "没有此用户的等级信息");
            }
            userDetail.setUserLevel(level);
            userDetail.setUserLevelName(expService.getLevelName(level));
            return RestBean.success(userDetail);
        }
        catch (UserException e){
            System.out.println("getUserInfo 调用失败");

            return RestBean.failure(400,null);
        }
    }
    @PostMapping("/otherUserInfo")
    public ResponseEntity<RestBean> getOtherUserInfo(@RequestBody GetOtherUserInfoRequest getOtherUserInfoRequest,
                                                     HttpServletRequest request){
        System.out.println("getUserInfo 调用成功");
        try{
            String userId = getOtherUserInfoRequest.getUserId();
            //System.out.println("userId:"+userId);
            UserDetail userDetail = userService.getUserInfo(userId);
            int exp = expService.readExp(Integer.parseInt(userId));
            if(exp < 0){
                return RestBean.failure(HttpStatus.NOT_FOUND, "没有此用户的经验信息");
            }
            userDetail.setExp(exp);

            int level = expService.getLevel(Integer.parseInt(userId));
            if(level < 0){
                return RestBean.failure(HttpStatus.NOT_FOUND, "没有此用户的等级信息");
            }
            userDetail.setUserLevel(level);
            userDetail.setUserLevelName(expService.getLevelName(level));
            return RestBean.success(userDetail);
        }
        catch (UserException e){
            System.out.println("getUserInfo 调用失败");

            return RestBean.failure(400,null);
        }
    }
    @PutMapping("/updateUserInfo")
    public ResponseEntity<RestBean>  updateAccountInfo(@RequestBody UpdateAccountInfoRequest updateAccountInfoRequest, HttpServletRequest request){
        System.out.println("updateAccountInfo 调用成功");
        String userId = (String) request.getAttribute("userId");
        if(userId == null) return RestBean.failure(400,"用户不存在");
        int idInt = Integer.parseInt(userId);
        if (idInt >= 10000000) {
            if (idInt != updateAccountInfoRequest.getId()) {
                return RestBean.failure(HttpStatus.FORBIDDEN, "无权限修改");
            }
        }

        String reason=null;

        if(updateAccountInfoRequest.getName() != null && updateAccountInfoRequest.getName() != ""){
            reason = examineService.TextCensor(updateAccountInfoRequest.getName());
            if(reason != null){
                return RestBean.failure(HttpStatus.UNPROCESSABLE_ENTITY, reason);
            }
        }

        if(updateAccountInfoRequest.getProfilePhotoUrl() != null){
            ossService.submitFile(updateAccountInfoRequest.getProfilePhotoUrl());
        }
        if(!userService.updateAccountInfo(updateAccountInfoRequest))
            return RestBean.failure(HttpStatus.NOT_FOUND,"用户不存在");
        return RestBean.success("修改成功");
    }
    @PostMapping("/userListByPage")
    public ResponseEntity<RestBean> getAccountInfoList(@RequestBody GetAccountInfoListRequest getAccountInfoListRequest, HttpServletRequest request){
        String adminId = (String) request.getAttribute("userId");
        if(adminId == null || Integer.parseInt(adminId)>10000000) return RestBean.failure(HttpStatus.BAD_REQUEST,"权限等级不足");
        System.out.println("getAccountInfoList 调用成功");
        Page<UserDetail> userDetailPage = userService.getUserDetailList(getAccountInfoListRequest);
        for(UserDetail userDetail: userDetailPage.getList()){
            int userId = userDetail.getId();
            int exp = expService.readExp(userId);
            if(exp < 0){
                return RestBean.failure(HttpStatus.NOT_FOUND, "没有此用户的经验信息");
            }
            userDetail.setExp(exp);

            int level = expService.getLevel(userId);
            if(level < 0){
                return RestBean.failure(HttpStatus.NOT_FOUND, "没有此用户的等级信息");
            }
            userDetail.setUserLevel(level);
            userDetail.setUserLevelName(expService.getLevelName(level));
        }
        return RestBean.success(userDetailPage);
    }

    @PostMapping("/getPwd")
    public ResponseEntity<RestBean> getPassword(@RequestBody GetPasswordRequest getPasswordRequest, HttpServletRequest request){
        String email = getPasswordRequest.getEmail();
        User user = userService.findUserByEmail(email);
        if(user == null) return RestBean.failure(HttpStatus.NOT_FOUND, "用户不存在");
        StringBuilder code= new StringBuilder();
        Random rand=new Random();//生成随机数
        for(int a=0;a<6;a++){
            code.append(rand.nextInt(10));//生成6位新密码
        }
        System.out.println("getPassword得到的email是:"+email);
        System.out.println("新密码是"+code);
        mailService.sendSimpleMail("YiYan_LAL@163.com",
        email,
        null,
        "【易言学习生活平台】找回密码",
        "欢迎使用找回密码功能！\n" +
                "你的新密码是："+code);
        String newPassword = code.toString();
        userService.resetPassword(email,newPassword);
        return RestBean.success("密码已重置并发送到你的邮箱");
    }
    @PutMapping("/updatePwd")
    public ResponseEntity<RestBean> updatePassword(@RequestBody UpdatePasswordRequest updatePasswordRequest, HttpServletRequest request) {
        //从request的token中获取用户id
        //String id = "10000001";
        String id = (String) request.getAttribute("userId");
        String oldPassword = updatePasswordRequest.getOldPassword();
        String newPassword = updatePasswordRequest.getNewPassword();
        System.out.println(id);
        String salt = userService.getUserSalt(updatePasswordRequest.getUserId());
        System.out.println("salt is "+salt);
        oldPassword = saltEncryption(oldPassword, salt);
        newPassword = saltEncryption(newPassword,salt);
        int idInt = Integer.parseInt(id);
        if(idInt<10000000){
            userService.updatePassword(updatePasswordRequest.getUserId(), newPassword);
            return RestBean.success("修改成功");
        }else{

            VerifyUserLoginRequest verifyUserLoginRequest = new VerifyUserLoginRequest();
            verifyUserLoginRequest.setIdOrEmail(id);
            verifyUserLoginRequest.setPassword(oldPassword);
            try{
                User user = userService.authenticateUser(verifyUserLoginRequest);
                userService.updatePassword(String.valueOf(user.getId()), newPassword);
                return RestBean.success("修改成功");
            }catch (UserException e){
                return RestBean.failure(HttpStatus.UNAUTHORIZED, "原密码错误");
            }
        }


    }
    @DeleteMapping("/deleteUser")
    public ResponseEntity<RestBean> deleteAccount(@RequestBody DeleteAccountRequest deleteAccountRequest, HttpServletRequest request){
        String userId = (String) request.getAttribute("userId");
        String deleteId = deleteAccountRequest.getId();
        if(Integer.parseInt(userId)>10000000) {
            deleteId = userId;
        }
        if (!userService.deleteAccount(deleteId)) {
            System.out.println("deleteUser失败:不存在此用户");
            return RestBean.failure(404, "不存在此用户");
        }
        return RestBean.success("删除成功");
    }
    public static String saltEncryption(String password, String salt){
        return password;
    }
}