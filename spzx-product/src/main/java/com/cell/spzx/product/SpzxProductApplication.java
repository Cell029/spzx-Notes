package com.cell.spzx.product;

import com.cell.spzx.common.log.annotation.EnableLogAspect;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableAsync
@EnableCaching
@EnableDiscoveryClient
@EnableRedisHttpSession
@MapperScan({
        "com.cell.spzx.common.log.mapper",
        "com.cell.spzx.product.mapper"
})
@EnableFeignClients(basePackages = "com.cell")
@SpringBootApplication(scanBasePackages = {"com.cell"})
public class SpzxProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpzxProductApplication.class, args);
    }

}
