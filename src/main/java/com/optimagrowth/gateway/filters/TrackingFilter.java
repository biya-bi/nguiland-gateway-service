package com.optimagrowth.gateway.filters;

import static com.optimagrowth.gateway.util.FilterUtil.CORRELATION_ID;

import java.util.UUID;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.optimagrowth.gateway.util.FilterUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Order(1)
@Component
class TrackingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        var httpHeaders = exchange.getRequest().getHeaders();
        var correlationId = FilterUtil.getCorrelationId(httpHeaders);

        if (correlationId != null) {
            log.debug("{} found in tracking filter: {}. ", CORRELATION_ID, correlationId);
        } else {
            correlationId = generateCorrelationId();
            exchange = FilterUtil.setCorrelationId(exchange, correlationId);
            log.debug("{} generated in tracking filter: {}.", CORRELATION_ID, correlationId);
        }

        return chain.filter(exchange);
    }

    private String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }

}