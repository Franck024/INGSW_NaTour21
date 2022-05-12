package com.example.natour21.exceptions;

public class UninitializedUserStompClientException extends RuntimeException {
    public UninitializedUserStompClientException(){super();}

    public UninitializedUserStompClientException(String message){
        super(message);
    }
}
