package com.cell.spzx.common.config;

import com.cell.spzx.common.interceptor.LoginUserInterceptor;
import com.cell.spzx.common.properties.AuthUrlProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableConfigurationProperties(value = {AuthUrlProperties.class})
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Autowired
    private LoginUserInterceptor loginUserInterceptor;
    @Autowired
    private AuthUrlProperties authUrlProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        List<String> noAuthUrls = authUrlProperties.getNoAuthUrls();
        noAuthUrls.add("/webjars/**");
        noAuthUrls.add("/doc.html");
        noAuthUrls.add("/swagger-ui.html");
        registry.addInterceptor(loginUserInterceptor)
                .excludePathPatterns(noAuthUrls)
                .addPathPatterns("/**");
    }

}
