package com.rahulsproject.connectu.api_gateway.filters;

import com.rahulsproject.connectu.api_gateway.exception.JwtException;
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

            log.info("Login request: {}", exchange.getRequest().getURI());

            if(!config.enabled) return chain.filter(exchange);

            final String authorizationHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

            if(authorizationHeader == null || !authorizationHeader.startsWith("Bearer")){
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                log.error("Authorization token header not found");
                return exchange.getResponse().setComplete();
            }

            final String token = authorizationHeader.split("Bearer ")[1];

           try{
               String userId = jwtService.getUserIdFromToken(token);
               String userEmail = jwtService.getEmailFromToken(token);

               ServerHttpRequest request = exchange.getRequest()
                       .mutate()
                       .header("X-User-Id", userId)
                       .header("X-User-Email", userEmail)
                       .build();

               ServerWebExchange mutatedExchange = exchange
                       .mutate()
                       .request(request)
                       .build();

               return chain.filter(mutatedExchange);
           }catch (JwtException e){
               log.error("JWT Exception: {}", e.getLocalizedMessage());
               exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
               return exchange.getResponse().setComplete();
           }
        };
    }

    @Data
    public static class Config{
        private boolean enabled;
    }
}
