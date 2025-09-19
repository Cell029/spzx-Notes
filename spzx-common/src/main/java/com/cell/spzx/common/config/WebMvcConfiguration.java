package com.cell.spzx.common.config;

import com.cell.spzx.common.interceptor.LoginUserInterceptor;
import com.cell.spzx.common.properties.AuthUrlProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(value = {AuthUrlProperties.class})
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private LoginUserInterceptor loginUserInterceptor;
    @Autowired
    private AuthUrlProperties authUrlProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        /*registry.addInterceptor(loginUserInterceptor)
                .excludePathPatterns(
                        "/doc.html",            // Knife4j doc 页面
                        "/swagger-ui.html",     // SpringDoc UI 页面
                        "/v3/api-docs/**",      // JSON 文档接口
                        "/swagger-ui/**",       // SpringDoc 静态资源
                        "/webjars/**",          // webjars 静态资源
                        "/auth-server/loginWithPhoneCode",
                        "/auth-server/generatePhoneCode",
                        "/auth-server/login",
                        "/auth-server/generateRandomCode"
                        )
                .addPathPatterns("/**");*/
        registry.addInterceptor(loginUserInterceptor)
                .excludePathPatterns(authUrlProperties.getNoAuthUrls())
                .addPathPatterns("/**");
    }
}
