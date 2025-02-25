package com.photo.pcloudbackend.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SaTokenConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册 Sa-Token 拦截器
        registry.addInterceptor(new SaInterceptor(handle -> {
            SaRouter.match("/**")    // 拦截所有请求
                    .notMatch(        // 排除不需要登录的接口
                            "/api/user/login",      // 登录接口
                            "/api/user/register",   // 注册接口
                            "/api/user/get/login"   // 获取登录信息接口
                    )
                    .notMatch("/**", "OPTIONS")     // 排除所有 OPTIONS 请求
                    .check(r -> StpUtil.checkLogin());
        })).addPathPatterns("/**");
    }
}