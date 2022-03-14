package org.ingsw21.backend.exceptions;

//import org.springframework.web.bind.annotation.ResponseStatus;
//import org.springframework.http.HttpStatus;

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
