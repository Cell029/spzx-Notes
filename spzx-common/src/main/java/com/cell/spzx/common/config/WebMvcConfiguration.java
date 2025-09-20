package com.cell.spzx.common.config;

import com.cell.spzx.common.interceptor.LoginUserInterceptor;
import com.cell.spzx.common.properties.AuthUrlProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
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
        registry.addInterceptor(loginUserInterceptor)
                .excludePathPatterns(authUrlProperties.getNoAuthUrls())
                .addPathPatterns("/**");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")   // 添加路径规则
                .allowCredentials(true)         // 是否允许在跨域的情况下传递Cookie
                .allowedOriginPatterns("*")     // 允许请求来源的域规则
                .allowedMethods("*")
                .allowedHeaders("*") ;          // 允许所有的请求头
    }
}
