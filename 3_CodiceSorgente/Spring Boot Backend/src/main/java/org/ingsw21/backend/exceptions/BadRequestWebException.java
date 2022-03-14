package org.ingsw21.backend.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import lombok.NoArgsConstructor;

import org.springframework.http.HttpStatus;
@NoArgsConstructor
@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class BadRequestWebException extends RuntimeException {
	public BadRequestWebException(String message) {
		super(message);
	}

}
