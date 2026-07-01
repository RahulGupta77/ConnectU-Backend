package com.rahulsproject.connectu.api_gateway.exception;

public class JwtException extends RuntimeException{
    public JwtException(String message){
        super(message);
    }
}
