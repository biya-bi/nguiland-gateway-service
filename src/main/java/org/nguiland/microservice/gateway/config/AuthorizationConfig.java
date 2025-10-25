package org.nguiland.microservice.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "security.authorization")
class AuthorizationConfig {
    private String[] allowedEndpoints;
}
