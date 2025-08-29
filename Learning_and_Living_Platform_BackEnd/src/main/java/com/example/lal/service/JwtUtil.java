package com.example.lal.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.lal.model.domain.User;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class JwtUtil {
    /**
     * 生成token
     */
    public static String createToken(User user) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 60); //默认令牌过期时间60分钟
        JWTCreator.Builder builder = JWT.create();
        builder.withClaim("id", String.valueOf(user.getId()))
                .withClaim("name", user.getName());

        return builder.withExpiresAt(calendar.getTime())
                .sign(Algorithm.HMAC256("123456"));
    }

    /**
     * 解析token
     */
    public static DecodedJWT verifyToken(String token) {
        if (token==null){
            System.out.println("token不能为空");
            return null;
        }
        //获取登录用户真正的密码假如数据库查出来的是123456
        String password = "123456";
        JWTVerifier build = JWT.require(Algorithm.HMAC256(password)).build();
        return build.verify(token);
    }
    public static String getUserId(String token){
        DecodedJWT decodedJWT = verifyToken(token);
        return decodedJWT.getClaim("id").asString();
    }
}

