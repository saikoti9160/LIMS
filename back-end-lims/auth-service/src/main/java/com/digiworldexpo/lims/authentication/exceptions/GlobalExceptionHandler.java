package com.digiworldexpo.lims.authentication.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.digiworldexpo.lims.authentication.model.ResponseModel;

//@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RecordNotFoundException.class)
	public ResponseEntity<ResponseModel<String>> handleRecordNotFoundException(RecordNotFoundException ex) {
		String errorMessage = "Record not found: " + ex.getMessage();
		ResponseModel<String> responseModel = new ResponseModel<>();
		responseModel.setMessage(errorMessage);
		responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseModel);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ResponseModel<String>> handleIllegalArgumentException(IllegalArgumentException ex) {
		String errorMessage = "Invalid Argument: " + ex.getMessage();
		ResponseModel<String> responseModel = new ResponseModel<>();
		responseModel.setMessage(errorMessage);
		responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseModel);
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ResponseModel<String>> handleBadRequestException(BadRequestException ex) {
		String errorMessage = "Bad Request: " + ex.getMessage();
		ResponseModel<String> responseModel = new ResponseModel<>();
		responseModel.setMessage(errorMessage);
		responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseModel);
	}

	@ExceptionHandler(DuplicateRecordFoundException.class)
	public ResponseEntity<ResponseModel<String>> handleDuplicateRecordFoundException(DuplicateRecordFoundException ex) {
		String errorMessage = "Duplicate Record: " + ex.getMessage();
		ResponseModel<String> responseModel = new ResponseModel<>();
		responseModel.setMessage(errorMessage);
		responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(responseModel);
	}

	@ExceptionHandler(UnauthorizedAccessException.class)
	public ResponseEntity<ResponseModel<String>> handleUnauthorizedAccessException(UnauthorizedAccessException ex) {
		String errorMessage = "Unauthorized Access: " + ex.getMessage();
		ResponseModel<String> responseModel = new ResponseModel<>();
		responseModel.setMessage(errorMessage);
		responseModel.setStatusCode(HttpStatus.UNAUTHORIZED.toString());
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseModel);
	}

	@ExceptionHandler(ForbiddenException.class)
	public ResponseEntity<ResponseModel<String>> handleForbiddenException(ForbiddenException ex) {
		String errorMessage = "Forbidden: " + ex.getMessage();
		ResponseModel<String> responseModel = new ResponseModel<>();
		responseModel.setMessage(errorMessage);
		responseModel.setStatusCode(HttpStatus.FORBIDDEN.toString());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseModel);
	}

	// Handle all uncaught exceptions
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseModel<String>> handleAllExceptions(Exception ex) {
		String errorMessage = "An unexpected error occurred: " + ex.getMessage();
		ResponseModel<String> responseModel = new ResponseModel<>();
		responseModel.setMessage(errorMessage);
		responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseModel);
	}
}
