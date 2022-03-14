package org.ingsw21.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

@NoArgsConstructor
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ServerException extends RuntimeException {
	
	public ServerException(String message) {
		super(message);
	}
	

}
