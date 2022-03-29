package com.example.natour21.exceptions;

public class UninitializedUserSessionException extends RuntimeException {
    public UninitializedUserSessionException(String message){
        super(message);
    }
}
