package com.example.lal.mapper;

import com.example.lal.model.domain.User;
import com.example.lal.model.entity.UserDetail;
import com.example.lal.model.entity.UserOnline;
import com.example.lal.model.entity.UserSummary;
import com.example.lal.model.request.user.GetAccountInfoListRequest;
import com.example.lal.model.request.user.UpdateAccountInfoRequest;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface UserMapper {


    @Select("SELECT * FROM User WHERE email = #{email}")
    User getUserByEmail(String email);
    @Select("SELECT * FROM User WHERE id = #{id}")
    UserDetail getUserById(String id);

    @Select("SELECT * FROM User WHERE (id = #{idOrEmail} OR email = #{idOrEmail}) AND password = #{password}")
    User getUserByIdOrEmail(String idOrEmail,String password);
    @Select("SELECT id, name FROM User WHERE email = #{email}")
    UserSummary getUserSummaryByEmail(String email);
    @Insert("INSERT INTO User (email, password,registerTime,birthday,profilePhotoUrl) " +
            "VALUES (#{email}, #{password},#{registerTime},#{birthday},#{profilePhotoUrl})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int addUser(User user);
    @Update("UPDATE User SET name = CONCAT('user', id)  WHERE email = #{email}")
    boolean setUserInitName(String email);
    @Update("UPDATE User SET " +
            "name = #{name}," +
            "email = #{email}," +
            "gender = #{gender}, " +
            "birthday = #{birthday}," +
            "profilePhotoUrl = #{profilePhotoUrl} " +
            "WHERE id = #{id}")
    boolean updateUser(UpdateAccountInfoRequest updateAccountInfoRequest);

    @Select("SELECT COUNT(*) FROM User")
    int getUserCount();
    @Select("SELECT * FROM User where id != 10000000 limit #{filter},#{cntInPage}")
    List<UserDetail> getAllUserDetail(int filter, int cntInPage);

    @Update("UPDATE User SET password = #{password} WHERE email = #{email}")
    boolean resetPasswordByEmail(String password, String email);

    @Update("UPDATE User SET password = #{password} WHERE id = #{id}")
    boolean updatePasswordById(String password, String id);

    @Delete("DELETE FROM User WHERE id = #{id}")
    boolean deleteUserById(String id);

    @Select("SELECT name FROM User WHERE id=#{userId};")
    String getNameById(int userId);

    @Select("SELECT profilePhotoUrl FROM User WHERE id=#{userId};")
    String getProfilePhotoUrl(int userId);

    @Select("SELECT salt from User WHERE (id = #{idOrEmail} OR email = #{idOrEmail})")
    String getUserSaltByIdOrEmail(String idOrEmail);

    @Insert("INSERT  INTO UserOnline" +
            " (id,name,password,email,gender,birthday,registerTime,profilePhotoUrl,LogInNum) " +
            "VALUES (#{id},#{name},#{password},#{email},#{gender},#{birthday},#{registerTime},#{profilePhotoUrl},#{LogInNum})")
    int addUserOnline(User user);
    @Update("UPDATE UserOnline SET " +
            "token = #{token} " +
            "where id = #{id}")
    int setUserOnlineToken(@Param("id") int id,@Param("token") String token);

    @Select("SELECT * FROM UserOnline")
    List<UserOnline> getAllUserOnline();

    @Delete("DELETE FROM UserOnline WHERE id = #{id}")
    void removeUserOnlineById(String id);

    @Update("UPDATE User SET LogInNum = #{logInNum} WHERE id = #{userId}")
    void setLoginNum(String userId, int logInNum);

    @Select("SELECT COUNT(*) FROM User " +
            "WHERE registerTime >= #{startTime} AND registerTime < #{endTime}")
    int getUserCountByTime(LocalDateTime startTime, LocalDateTime endTime);

    @Select("SELECT MAX(id) FROM User")
    int getMaxUserId();

    @Select("SELECT COUNT(*) FROM UserOnline" +
            " where id>10000000")
    int getAllUserOnlineCount();
}
