package com.cell.spzx.auth_server;

import com.cell.spzx.common.config.MinioConfig;
import com.cell.spzx.common.properties.AuthUrlProperties;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableDiscoveryClient
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.cell")
@MapperScan({
        "com.cell.spzx.common.log.mapper", // 扫描 log 的 mapper
        "com.cell.spzx.auth_server.mapper" // 自己模块的 mapper
})
@SpringBootApplication(scanBasePackages = {"com.cell"})
public class AuthServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServerApplication.class, args);
    }

}
