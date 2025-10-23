package org.nguiland.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.jwt")
class JwtConfig {
    private String[] issuers;
}
