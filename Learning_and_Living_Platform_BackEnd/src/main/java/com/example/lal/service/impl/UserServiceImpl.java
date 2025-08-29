package com.example.lal.service.impl;

import com.example.lal.mapper.AdminMapper;
import com.example.lal.mapper.UserMapper;
import com.example.lal.model.domain.Admin;
import com.example.lal.model.domain.User;
import com.example.lal.model.entity.*;
import com.example.lal.model.exceptions.UserException;
import com.example.lal.model.request.user.*;
import com.example.lal.service.JwtUtil;
import com.example.lal.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Resource
    private AdminMapper adminMapper;
    //@Autowired
    //private Page<UserDetail> userDetailPage;
    @Override
    public Admin getAdminByEmail(String email){
        return adminMapper.getAdminByEmail(email);
    }
    @Override
    public void updateAdmin(Admin admin){
        adminMapper.updateAdmin(admin);
    }
    @Override
    public String getUserIdByToken(String token) {
        return JwtUtil.getUserId(token);
    }
    @Override
    public User authenticateAdmin(VerifyAdminLoginRequest verifyAdminLoginRequest) throws UserException {
        User admin = adminMapper.verifyAdminByIdOrEmail(verifyAdminLoginRequest.getAccount(),verifyAdminLoginRequest.getPassword());
        if(admin==null)
            throw new UserException("管理员不存在或密码错误");
        return admin;
    }
    @Override
    public User authenticateUser(VerifyUserLoginRequest verifyUserLoginRequest) throws UserException {
        User user = userMapper.getUserByIdOrEmail(verifyUserLoginRequest.getIdOrEmail(),verifyUserLoginRequest.getPassword());
        if(user==null)
            throw new UserException("用户不存在或密码错误1");
        return user;
    }
    @Override
    public void addUserOnline(User user,String token)throws DataIntegrityViolationException {
        userMapper.addUserOnline(user);
        userMapper.setUserOnlineToken(user.getId(),token);
    }
    @Override
    public void setUserOnlineToken(int id, String token){
        userMapper.setUserOnlineToken(id,token);
    }
    @Override
    public List<UserOnline> getAllUserOnline(){
        return userMapper.getAllUserOnline();
    }

    @Override
    public int getMaxUserId(){
        return userMapper.getMaxUserId();
    }
    @Override
    public String addUser(VerifyRegisterRequest verifyRegisterRequest) throws UserException{
        UserSummary userSummary = userMapper.getUserSummaryByEmail(verifyRegisterRequest.getEmail());
        if(userSummary!=null)
            throw new UserException("该邮箱已被注册");
        User user = new User();

        user.setEmail(verifyRegisterRequest.getEmail());
        user.setPassword(verifyRegisterRequest.getPassword());
        user.setRegisterTime(new Date(System.currentTimeMillis()));

        user.setProfilePhotoUrl("https://learning-and-living.oss-cn-beijing.aliyuncs.com/test/ava1.webp");
        user.setBirthday(new Date(System.currentTimeMillis()));
        userMapper.addUser(user);
        userMapper.setUserInitName(verifyRegisterRequest.getEmail());

        return String.valueOf(user.getId());
    }
    @Override
    public String addAdmin(VerifyRegisterRequest verifyRegisterRequest) throws UserException {

        if(adminMapper.getAdminByEmail(verifyRegisterRequest.getEmail())!=null)
            throw new UserException("该邮箱已被注册");
        Admin admin = new Admin();
        admin.setEmail(verifyRegisterRequest.getEmail());
        admin.setPassword(verifyRegisterRequest.getPassword());
        adminMapper.addAdmin(admin);
        return String.valueOf(admin.getId());
    }
    @Override
    public UserDetail getUserInfo(String userId) throws UserException {
        UserDetail user = userMapper.getUserById(userId);
        if(user==null)
            throw new UserException("用户不存在");
        return user;
    }
    @Override
    public boolean updateAccountInfo(UpdateAccountInfoRequest updateAccountInfoRequest) {
        UserDetail user = userMapper.getUserById(String.valueOf(updateAccountInfoRequest.getId()));
        if(user==null)
            return false;
        userMapper.updateUser(updateAccountInfoRequest);
        return true;
    }
    @Override
    public Page<UserDetail> getUserDetailList(GetAccountInfoListRequest getAccountInfoListRequest) {
        int currentPage = getAccountInfoListRequest.getPageNum();
        int pageSize = getAccountInfoListRequest.getCntInPage();
        Page<UserDetail> userDetailPage = new Page<>();
        //分页逻辑代码
        //Object allPageCount=redisTemplate.opsForValue().get("allPageCount");
        int count=0;
        //查询数据总条数  
        count=userMapper.getUserCount();
        //System.out.println(count);
        //数据总条数存入page对象  
        userDetailPage.setCount(count);
        //设置当前页数  
        userDetailPage.setCurrPage(currentPage);
        //设置每页条数  
        userDetailPage.setPageSize(pageSize);
        //设置过滤条数过滤前面页面数据  
        userDetailPage.setFilterCount((userDetailPage.getCurrPage()-1)* userDetailPage.getPageSize());
        //设置总页数，总页数+每页条数-1再运算可使总页数向上取整
        userDetailPage.setPageCount((userDetailPage.getCount()+ userDetailPage.getPageSize()-1)/ userDetailPage.getPageSize());
        //设置下一页  
        userDetailPage.setNextPage(userDetailPage.getCurrPage()== userDetailPage.getPageCount()? userDetailPage.getCurrPage(): userDetailPage.getCurrPage()+1);
        //设置上一页  
        userDetailPage.setPrePage(userDetailPage.getCurrPage()==1? userDetailPage.getCurrPage(): userDetailPage.getCurrPage()-1);
        //System.out.println(userDetailPage.getFilterCount());
        userDetailPage.setList(userMapper.getAllUserDetail(userDetailPage.getFilterCount(), userDetailPage.getPageSize()));

        //List<UserDetail> users = userMapper.getAllUserDetail(0,10);
        //System.out.println(users.size());
        return userDetailPage;
    }
    @Override
    public User findUserByEmail(String email) {
        return userMapper.getUserByEmail(email);
    }
    @Override
    public void resetPassword(String email, String code)
    {
        userMapper.resetPasswordByEmail(code,email);
    }
    @Override
    public void updatePassword(String id, String password)
    {
        userMapper.updatePasswordById(password,id);
    }
    @Override
    public boolean deleteAccount(String id)
    {
        if(userMapper.getUserById(id)==null)
            return false;
        userMapper.deleteUserById(id);
        return true;
    }

    @Override
    public String getProfilePhotoUrl(int userId){
        return userMapper.getProfilePhotoUrl(userId);
    }
    @Override
    public void setLoginNum(String userId, int logInNum){
        userMapper.setLoginNum(userId,logInNum);
    }

    @Override
    public void deleteUserOnline(String id){
        userMapper.removeUserOnlineById(id);
    }
    @Override
    public int getUserCountByTime(LocalDateTime startTime, LocalDateTime endTime){
        return userMapper.getUserCountByTime(startTime,endTime);
    }
    @Override
    public int getAllUserOnlineCount(){
        return userMapper.getAllUserOnlineCount();
    }
    @Override
    public  int getAllUserCount(){
        return userMapper.getUserCount();
    }
}
