package com.example.lal.service;


import com.example.lal.model.domain.Admin;
import com.example.lal.model.domain.User;
import com.example.lal.model.entity.Page;
import com.example.lal.model.entity.UserDetail;
import com.example.lal.model.entity.UserOnline;
import com.example.lal.model.exceptions.UserException;
import com.example.lal.model.request.user.*;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    String getUserIdByToken(String token);
    User authenticateAdmin(VerifyAdminLoginRequest verifyAdminLoginRequest)throws UserException;
    User authenticateUser(VerifyUserLoginRequest verifyUserLoginRequest)throws UserException;
    String addUser(VerifyRegisterRequest verifyRegisterRequest) throws UserException;
    String addAdmin(VerifyRegisterRequest verifyRegisterRequest)throws UserException;
    UserDetail getUserInfo(String userId)throws UserException;
    boolean updateAccountInfo(UpdateAccountInfoRequest updateAccountInfoRequest);
    Page<UserDetail> getUserDetailList(GetAccountInfoListRequest getAccountInfoListRequest);

    User findUserByEmail(String email);


    void resetPassword(String email, String code);
    void updatePassword(String id, String password);

    boolean deleteAccount(String id);

    Admin getAdminByEmail(String email);



    void updateAdmin(Admin admin);

    void addUserOnline(User user,String token)throws DataIntegrityViolationException;

    void setUserOnlineToken(int id, String token);

    List<UserOnline> getAllUserOnline();

    String getProfilePhotoUrl(int userId);

    void setLoginNum(String s, int logInNum);

    void deleteUserOnline(String id);

    int getUserCountByTime(LocalDateTime startTime, LocalDateTime endTime);

    int getMaxUserId();

    int getAllUserOnlineCount();

    int getAllUserCount();
}
