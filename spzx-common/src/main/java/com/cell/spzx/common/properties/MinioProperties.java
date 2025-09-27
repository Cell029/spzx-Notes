package com.cell.spzx.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spzx.minio")
public class MinioProperties {
    private String accessKey;
    private String secretKey;
    private String endpointUrl;
    private String bucketName;
    private String region;
}
