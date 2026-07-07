package com.rahulsproject.connectu.posts_service.exception;


public class ConstraintViolationException extends RuntimeException{
    public ConstraintViolationException(String message){
        super(message);
    }
}
