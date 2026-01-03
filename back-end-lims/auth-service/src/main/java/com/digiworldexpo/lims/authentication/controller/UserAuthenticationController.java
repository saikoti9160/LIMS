package com.digiworldexpo.lims.authentication.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.services.cognitoidp.model.ChangePasswordResult;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.digiworldexpo.lims.authentication.constants.AuthConstants;
import com.digiworldexpo.lims.authentication.exceptions.ValueNotFoundException;
import com.digiworldexpo.lims.authentication.model.LoginRequest;
import com.digiworldexpo.lims.authentication.model.PasswordChangeRequest;
import com.digiworldexpo.lims.authentication.model.ResponseModel;
import com.digiworldexpo.lims.authentication.model.SignInResponse;
import com.digiworldexpo.lims.authentication.model.UpdatePasswordRequest;
import com.digiworldexpo.lims.authentication.model.UserModel;
import com.digiworldexpo.lims.authentication.service.AuthService;
import com.digiworldexpo.lims.authentication.service.UserAuthenticationService;
import com.digiworldexpo.lims.authentication.util.HttpStatusCode;
import com.digiworldexpo.lims.entities.User;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/user")
@Slf4j
@CrossOrigin
public class UserAuthenticationController {

	@Autowired
	UserAuthenticationService userAuthenticationService;

	@Autowired
	HttpStatusCode httpStatusCode;

	@Autowired
	AuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<ResponseModel<User>> registerUser(@RequestBody UserModel userModel) {
		log.info("Begin UserAuthentication Controller -> registerUser() method");
		ResponseModel<User> response = userAuthenticationService.registerUserInCognito(userModel);
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		log.info("End UserAuthentication Controller -> registerUser() method");
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseModel<SignInResponse>> login(@RequestBody LoginRequest loginRequest) {
		log.info("Begin UserAuthentication Controller -> login() method");
		ResponseModel<SignInResponse> response = userAuthenticationService
				.initiateLogin(loginRequest.getEmail().toLowerCase(), loginRequest.getPassword());
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		log.info("End UserAuthentication Controller -> login() method");
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/forgot-password")
	public ResponseEntity<ResponseModel<ForgotPasswordResult>> forgotPassword(@RequestParam("email") String email)
			throws ValueNotFoundException {
		log.info("Begin UserAuthentication Controller -> forgotPassword() method");
		ResponseModel<ForgotPasswordResult> response = userAuthenticationService.forgotPassword(email);
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		log.info("End UserAuthentication Controller -> forgotPassword() method");
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PutMapping("/update-password")
	public ResponseEntity<ResponseModel<ChangePasswordResult>> updatePassword(
			@RequestBody UpdatePasswordRequest request, HttpServletRequest req) throws ValueNotFoundException {
		log.info("Begin UserAuthentication Controller -> updatePassword() method");
		request.setToken(req.getHeader(AuthConstants.AUTHORIZATION).substring(7));
		ResponseModel<ChangePasswordResult> response = userAuthenticationService.resetPassword(request);
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		log.info("End UserAuthentication Controller -> updatePassword() method");
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/check-email")
	public ResponseEntity<ResponseModel<String>> checkEmailExists(@RequestParam String email) {
		log.info("Begin UserAuthentication Controller -> checkEmailExists() method");
		ResponseModel<String> response = userAuthenticationService.checkEmailExists(email);
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		log.info("End UserAuthentication Controller -> checkEmailExists() method");
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PutMapping("/update-status")
	public ResponseEntity<ResponseModel<User>> updateStatus(@RequestParam String email) {
		log.info("Begin UserAuthentication Controller -> updateStatus() method");
		ResponseModel<User> response = userAuthenticationService.updateStatus(email);
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		log.info("End UserAuthentication Controller -> updateStatus() method");
		return ResponseEntity.status(httpStatus).body(response);

	}

	@PostMapping("/reset-password")
	public ResponseEntity<ResponseModel<ConfirmForgotPasswordResult>> resetPassword(
			@RequestBody PasswordChangeRequest psRequest) {
		log.info("Begin UserAuthentication Controller -> resetPassword() method");
		ResponseModel<ConfirmForgotPasswordResult> response = userAuthenticationService
				.updatePasswordWithVerificationCode(psRequest);
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		log.info("End UserAuthentication Controller -> resetPassword() method");
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/resend-verification")
	public ResponseEntity<ResponseModel<Boolean>> resendVerificationEmail(@RequestParam String email) {
		log.info("Begin UserAuthentication Controller -> resendVerificationEmail() method");
		ResponseModel<Boolean> response = userAuthenticationService.resendVerificationMail(email);
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		log.info("End UserAuthentication Controller -> resendVerificationEmail() method");
		return ResponseEntity.status(httpStatus).body(response);
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<ResponseModel<SignInResponse>> getAccessTokenAfterExpiry(
			@RequestHeader("refreshToken") String refreshToken) throws Exception {
		log.info("Begin UserAuthentication Controller -> getAccessTokenAfterExpiry() method");
		ResponseModel<SignInResponse> response = authService.getAuthTokenAfterExpiry(refreshToken);
		HttpStatus httpStatus = httpStatusCode.getHttpStatusFromCode(response.getStatusCode());
		log.info("End UserAuthentication Controller -> getAccessTokenAfterExpiry() method");
		return ResponseEntity.status(httpStatus).body(response);
	}

}
