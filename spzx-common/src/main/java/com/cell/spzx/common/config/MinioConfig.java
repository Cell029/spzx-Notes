package com.cell.spzx.common.config;

import com.cell.spzx.common.properties.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    private final MinioProperties minioProperties;

    // 构造器注入
    public MinioConfig(MinioProperties minioProperties) {
        this.minioProperties = minioProperties;
    }

    // 初始化 MinioClient
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioProperties.getEndpointUrl())
                .credentials(minioProperties.getAccessKey(), minioProperties.getSecretKey())
                .build();
    }

    // 创建 ApplicationRunner Bean，接收 MinioClient 作为参数
    @Bean
    public ApplicationRunner minioInitializer(MinioClient minioClient) {
        return args -> {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(minioProperties.getBucketName()).build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioProperties.getBucketName()).build());
            }

            // 设置桶策略为公开读取（download）
            String policyJson = "{\n" +
                    "  \"Version\":\"2012-10-17\",\n" +
                    "  \"Statement\":[{\n" +
                    "    \"Effect\":\"Allow\",\n" +
                    "    \"Principal\":\"*\",\n" +
                    "    \"Action\":[\"s3:GetObject\"],\n" +
                    "    \"Resource\":[\"arn:aws:s3:::" + minioProperties.getBucketName() + "/*\"]\n" +
                    "  }]\n" +
                    "}";

            minioClient.setBucketPolicy(
                    io.minio.SetBucketPolicyArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .config(policyJson)
                            .build()
            );
        };
    }

}
