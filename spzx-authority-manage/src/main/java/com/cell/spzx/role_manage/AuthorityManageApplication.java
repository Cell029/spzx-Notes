package com.cell.spzx.role_manage;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableScheduling
@EnableFeignClients
@EnableDiscoveryClient
@EnableRedisHttpSession
@MapperScan("com.cell.spzx.role_manage.mapper")
@SpringBootApplication(scanBasePackages = {"com.cell"})
public class AuthorityManageApplication {
    public static void main(String[] args) {
        SpringApplication.run(AuthorityManageApplication.class, args);
    }
}
