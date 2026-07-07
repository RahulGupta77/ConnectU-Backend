package com.rahulsproject.connectu.posts_service.exception;

public class DataIntegrityViolationException extends RuntimeException{
    public DataIntegrityViolationException(String message){
        super(message);
    }
}
