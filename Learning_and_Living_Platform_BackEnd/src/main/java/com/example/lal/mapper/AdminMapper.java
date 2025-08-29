package com.example.lal.mapper;


import com.example.lal.model.domain.Admin;
import com.example.lal.model.domain.User;
import org.apache.ibatis.annotations.*;


@Mapper
public interface AdminMapper {
    @Select("SELECT * FROM Admin WHERE (id = #{idOrEmail} OR email = #{idOrEmail}) AND password = #{password}")
    User verifyAdminByIdOrEmail(String idOrEmail, String password);
    @Select("SELECT * FROM Admin WHERE email = #{email}")
    Admin getAdminByEmail(String email);
    @Insert("INSERT INTO Admin (email, password) VALUES (#{email}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    boolean addAdmin(Admin admin);
    @Update("UPDATE Admin SET " +
            "password = #{password} " +
            "where id = #{id} ")
    boolean updateAdmin(Admin admin);
}
