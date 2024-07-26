package com.optimagrowth.gateway.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity.AuthorizeExchangeSpec;
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
            @Nullable @Value("${ostock.api.authentication.allowedEndpoints:#{null}}") String[] allowedEndpoints) {
        return httpSecurity.authorizeExchange(spec -> authenticated(spec, allowedEndpoints))
                .oauth2ResourceServer(spec -> spec.authenticationEntryPoint(entryPoint).jwt(Customizer.withDefaults()))
                .cors(Customizer.withDefaults())
                .exceptionHandling(spec -> spec.authenticationEntryPoint(entryPoint))
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource(
            @Value("${security.cors.allowedOrigins:}") String[] allowedOrigins,
            @Value("${security.cors.allowedMethods:}") String[] allowedMethods,
            @Value("${security.cors.allowedHeaders:}") String[] allowedHeaders) {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins));
        configuration.setAllowedMethods(Arrays.asList(allowedMethods));
        configuration.setAllowedHeaders(Arrays.asList(allowedHeaders));

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    private AuthorizeExchangeSpec authenticated(AuthorizeExchangeSpec authorizeExchangeSpec, String[] allowedEndpoints) {
        var spec = allowedEndpoints != null ? authorizeExchangeSpec.pathMatchers(allowedEndpoints).permitAll() : authorizeExchangeSpec;

        return spec.anyExchange().authenticated();
    }
}