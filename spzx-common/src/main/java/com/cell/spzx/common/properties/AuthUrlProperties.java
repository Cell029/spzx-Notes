package com.cell.spzx.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "spzx.auth")
public class AuthUrlProperties {
    private List<String> noAuthUrls;
}
