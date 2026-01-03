package com.digiworldexpo.lims.authentication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(value = HttpStatus.CONFLICT)
public class DuplicateRecordFoundException extends RuntimeException{

	private String message;

	public DuplicateRecordFoundException(String message) {
		super(message);
		this.message = message;
	}
	
	
}

