package com.cell.common.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseKnife4jConfig {

    /**
     * 提供一个创建分组的方法，供子模块重写或调用
     * @param groupName 分组名
     * @param paths 要匹配的路径
     * @return GroupedOpenApi
     */
    public GroupedOpenApi adminApi(String groupName, String... paths) { // 创建了一个 api 接口的分组
        return GroupedOpenApi.builder()
                .group("admin-api") // 分组名称
                .pathsToMatch("/admin/**") // 接口请求路径规则
                .build();
    }

    /***
     * 自定义接口信息
     */
    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("尚品甑选 API 接口文档")
                        .version("1.0")
                        .description("尚品甑选 API 接口文档")
                        .contact(new Contact().name("cell")));
    }

}