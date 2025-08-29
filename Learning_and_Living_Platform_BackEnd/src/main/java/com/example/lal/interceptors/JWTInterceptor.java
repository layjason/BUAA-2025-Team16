package com.example.lal.interceptors;

import com.auth0.jwt.exceptions.AlgorithmMismatchException;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.lal.model.RestBean;
import com.example.lal.service.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Date;

import static com.example.lal.constant.OtherConstants.*;


@Slf4j
public class JWTInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String token = request.getHeader("token");
        log.info("token：" + token);
        System.out.println("拦截器开始工作");
        response.setContentType("application/json;charset=utf-8");
        if (token == null) {
            log.error("token为空");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置状态码为 401
            ObjectMapper objectMapper = new ObjectMapper();
            String errorResponseJson = objectMapper.writeValueAsString(new RestBean<>(HttpServletResponse.SC_UNAUTHORIZED, false, "Token is null"));
            response.getWriter().write(errorResponseJson); // 返回错误信息给前端
            return false;
        }
        if(token.equals(USER_TOKEN)){
            System.out.println("你现在使用的是万能用户token，它将帮助你通过拦截器，并设置userId为10000003");
            request.setAttribute("userId", "10000003");
            return true;
        }
        if(token.equals(ADMIN_TOKEN)){
            System.out.println("你现在使用的是万能管理员token，它将帮助你通过拦截器，并设置userId为2");
            request.setAttribute("userId", "2");
            return true;
        }
        try {
            DecodedJWT decodedJWT = JwtUtil.verifyToken(token);
            System.out.println("token中得到的userId:"+JwtUtil.getUserId(token));
            request.setAttribute("userId", JwtUtil.getUserId(token));
            // 获取过期时间
            Date expirationTime = decodedJWT.getExpiresAt();

            // 获取当前时间
            Date currentTime = new Date();

            // 计算剩余有效时间（以毫秒为单位）
            long remainingTime = expirationTime.getTime() - currentTime.getTime();

            System.out.println("Token 过期时间: " + expirationTime);
            System.out.println("剩余有效时间: " + remainingTime + " ms");

        } catch (SignatureVerificationException e) {
            log.error("无效签名");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置状态码为 401
            ObjectMapper objectMapper = new ObjectMapper();
            String errorResponseJson = objectMapper.writeValueAsString(new RestBean<>(HttpServletResponse.SC_UNAUTHORIZED, false, "Invalid or expired token"));
            response.getWriter().write(errorResponseJson); // 返回错误信息给前端
            return false;
        } catch (TokenExpiredException e) {
            log.error("token过期");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置状态码为 401
            ObjectMapper objectMapper = new ObjectMapper();
            String errorResponseJson = objectMapper.writeValueAsString(new RestBean<>(HttpServletResponse.SC_UNAUTHORIZED, false, "Invalid or expired token"));
            response.getWriter().write(errorResponseJson); // 返回错误信息给前端
            return false;
        } catch (AlgorithmMismatchException e) {
            log.error("token算法不一致");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置状态码为 401
            ObjectMapper objectMapper = new ObjectMapper();
            String errorResponseJson = objectMapper.writeValueAsString(new RestBean<>(HttpServletResponse.SC_UNAUTHORIZED, false, "Invalid or expired token"));
            response.getWriter().write(errorResponseJson); // 返回错误信息给前端
            return false;
        } catch (Exception e) {
            log.error("token无效");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 设置状态码为 401
            ObjectMapper objectMapper = new ObjectMapper();
            String errorResponseJson = objectMapper.writeValueAsString(new RestBean<>(HttpServletResponse.SC_UNAUTHORIZED, false, "Invalid or expired token"));
            response.getWriter().write(errorResponseJson); // 返回错误信息给前端
            return false;
        }
        return true;
    }
}

