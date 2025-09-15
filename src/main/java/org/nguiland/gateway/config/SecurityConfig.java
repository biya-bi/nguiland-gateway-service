package org.nguiland.gateway.config;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
import org.springframework.security.config.web.server.ServerHttpSecurity.OAuth2ResourceServerSpec;
import org.springframework.security.oauth2.server.resource.authentication.JwtIssuerReactiveAuthenticationManagerResolver;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity,
            ServerAuthenticationEntryPoint entryPoint,
            JwtConfig jwtConfig,
            AuthorizationConfig authorizationConfig) {
        return httpSecurity.authorizeExchange(spec -> authenticated(spec, authorizationConfig))
                .oauth2ResourceServer(configure(entryPoint, jwtConfig))
                .cors(Customizer.withDefaults())
                .exceptionHandling(spec -> spec.authenticationEntryPoint(entryPoint))
                .build();
    }

    @Bean
    @ConfigurationProperties(prefix = "security.cors")
    CorsConfiguration corsConfiguration() {
        return new CorsConfiguration();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(CorsConfiguration configuration) {
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private AuthorizeExchangeSpec authenticated(AuthorizeExchangeSpec authorizeExchangeSpec,
            AuthorizationConfig authorizationConfig) {
        var allowedEndpoints = authorizationConfig.getAllowedEndpoints();
        var spec = ObjectUtils.isNotEmpty(allowedEndpoints)
                ? authorizeExchangeSpec.pathMatchers(allowedEndpoints).permitAll()
                : authorizeExchangeSpec;

        return spec.anyExchange().authenticated();
    }

    private Customizer<OAuth2ResourceServerSpec> configure(ServerAuthenticationEntryPoint entryPoint,
            JwtConfig jwtConfig) {
        var resolver = JwtIssuerReactiveAuthenticationManagerResolver
                .fromTrustedIssuers(jwtConfig.getGoogleIssuerUri(), jwtConfig.getKeycloakIssuerUri());
        return spec -> spec.authenticationEntryPoint(entryPoint).authenticationManagerResolver(resolver);
    }

}