package com.rahulsproject.connectu.api_gateway.filters;

import com.rahulsproject.connectu.api_gateway.service.JwtService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

@Component
@Slf4j
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    private final JwtService jwtService;

    public AuthenticationGatewayFilterFactory(JwtService jwtService) {
        super(Config.class);
        this.jwtService = jwtService;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {
            // Performing authentication

            log.info("config: {}", config.toString());

            if(!config.enabled) return chain.filter(exchange);

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if(authorizationHeader == null){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String token = authorizationHeader.split("Bearer ")[1];

            Long userId = jwtService.getUserIdFromToken(token);

//            exchange.getRequest()
//                    .mutate()
//                    .header("X-User-Id", userId.toString())
//                    .build();

            ServerHttpRequest request = exchange.getRequest()
                    .mutate()
                    .header("X-User-Id", userId.toString())
                    .build();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(request)
                    .build();

            return chain.filter(mutatedExchange);
        };
    }

    @Data
    public static class Config{
        private boolean enabled;
    }
}
