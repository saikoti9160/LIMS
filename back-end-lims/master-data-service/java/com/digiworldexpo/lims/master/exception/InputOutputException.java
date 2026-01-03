package com.digiworldexpo.lims.master.exception;

 
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;
 
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class InputOutputException extends RuntimeException {
 
	private String message;
 
	public InputOutputException(String message) {
		super(message);
		this.message = message;
	}

}

