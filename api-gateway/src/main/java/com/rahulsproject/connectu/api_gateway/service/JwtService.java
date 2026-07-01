package com.rahulsproject.connectu.api_gateway.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class JwtService {
    @Value("${jwt.secretKey}")
    private String jwtSecret;

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public Long getUserIdFromToken(String token){
        try{
            Claims claim = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.valueOf(claim.getSubject());
        }catch (JwtException e){
            throw new JwtException(e.getMessage());
        }
    }

    public String getEmailFromToken(String token){
        try{
            Claims claim = Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claim.get("email", String.class);
        }catch (JwtException e){
            throw new JwtException(e.getMessage());
        }
    }


}
