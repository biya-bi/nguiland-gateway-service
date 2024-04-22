package com.optimagrowth.gateway.filter;

import static com.optimagrowth.gateway.util.FilterUtil.CORRELATION_ID;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.optimagrowth.gateway.util.FilterUtil;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
class ResponseFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        HttpHeaders httpHeaders = request.getHeaders();
        String correlationId = FilterUtil.getCorrelationId(httpHeaders);

        log.debug("Adding the correlation ID to the outbound headers: {}", correlationId);

        exchange.getResponse().getHeaders().add(CORRELATION_ID, correlationId);

        log.debug("Completing outgoing request for {}.", request.getURI());

        return chain.filter(exchange);
    }

}
