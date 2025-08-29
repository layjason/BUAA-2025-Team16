package com.example.lal.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class WebCorsConfig implements org.springframework.web.servlet.config.annotation.WebMvcConfigurer {
  @Override
  public void addCorsMappings(org.springframework.web.servlet.config.annotation.CorsRegistry registry) {
    registry.addMapping("/**")
        .allowedOrigins("http://localhost:5173")
        .allowedMethods("GET","POST","PUT","DELETE","OPTIONS")
        .allowedHeaders("*")
        .exposedHeaders("*")
        .allowCredentials(true)   // only if you need cookies/Authorization
        .maxAge(3600);
  }
}
