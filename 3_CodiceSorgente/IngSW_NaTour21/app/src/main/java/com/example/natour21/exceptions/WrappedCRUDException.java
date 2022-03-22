package com.example.natour21.exceptions;

public class WrappedCRUDException extends Exception {

    final Exception wrappedException;

    public WrappedCRUDException(Exception wrappedException)
    {
        this.wrappedException = wrappedException;
    }

    public Exception getWrappedException() {
        return wrappedException;
    }
}