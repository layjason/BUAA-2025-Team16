package com.example.lal.config;


import com.example.lal.interceptors.JWTInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;



@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new JWTInterceptor())
                //拦截的路径
                .addPathPatterns("/user/*",
                        "/post/*",
                        "/resource/*",
                        "/statistic/*")

                //排除接口
                .excludePathPatterns("/user/getSalt",
                        "/user/hello",
                        "/user/login",
                        "/user/register",
                        "/user/adminLogin",
                        "/user/getPwd",
                        "/post/getUserPost",
                        "/resource/getResDetail",
                        "/resource/listResByClassWithPage",
                        "/resource/searchResource");
    }
}

