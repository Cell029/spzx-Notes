package com.cell.spzx.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableCaching
@EnableDiscoveryClient
@EnableRedisHttpSession
@MapperScan("com.cell.spzx.product.mapper")
@SpringBootApplication(scanBasePackages = {"com.cell"})
public class SpzxProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpzxProductApplication.class, args);
    }

}
