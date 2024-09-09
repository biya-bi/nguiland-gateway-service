package org.nguiland.gateway.authentication;

import java.nio.charset.StandardCharsets;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.web.server.BearerTokenServerAuthenticationEntryPoint;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
class AuthenticationEntryPoint implements ServerAuthenticationEntryPoint {

    private final ServerAuthenticationEntryPoint entryPoint = new BearerTokenServerAuthenticationEntryPoint();
    private final ObjectMapper objectMapper;

    AuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException exception) {
        var result = entryPoint.commence(exchange, exception);
        if (exception instanceof InvalidBearerTokenException tokenException) {
            ServerHttpResponse response = exchange.getResponse();

            DataBuffer dataBuffer = getDataBuffer(tokenException, response);

            return Mono.just(dataBuffer)
                    .flatMap(buffer -> writeBuffer(buffer, response))
                    .doOnSuccess(empty -> DataBufferUtils.release(dataBuffer));
        }
        return result;
    }

    private DataBuffer getDataBuffer(InvalidBearerTokenException exception, ServerHttpResponse response) {
        String message;
        try {
            message = objectMapper.writeValueAsString(exception.getError());
        } catch (JsonProcessingException e) {
            log.error("An unexpected exception has occurred", exception);
            message = exception.getMessage();
        }
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        return response.bufferFactory().wrap(bytes);
    }

    private Mono<Void> writeBuffer(DataBuffer buffer, ServerHttpResponse response) {
        HttpHeaders headers = response.getHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentLength(buffer.readableByteCount());

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.writeWith(Mono.just(buffer));
    }

}
