package org.example.exception;

public class UserExistsException extends RuntimeException{
    public UserExistsException(String massage){
        super(massage);
    }
}
