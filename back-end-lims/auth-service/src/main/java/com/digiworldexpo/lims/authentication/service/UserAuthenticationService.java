package com.digiworldexpo.lims.authentication.service;

import com.amazonaws.services.cognitoidp.model.ChangePasswordResult;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.digiworldexpo.lims.authentication.model.PasswordChangeRequest;
import com.digiworldexpo.lims.authentication.model.ResponseModel;
import com.digiworldexpo.lims.authentication.model.SignInResponse;
import com.digiworldexpo.lims.authentication.model.UpdatePasswordRequest;
import com.digiworldexpo.lims.authentication.model.UserModel;

import com.digiworldexpo.lims.entities.User;

public interface UserAuthenticationService {
	
	public ResponseModel<User> registerUserInCognito(UserModel userModel);
	
	public ResponseModel<SignInResponse> initiateLogin(String email, String password);
	
	public ResponseModel<ForgotPasswordResult> forgotPassword(String email);
	
	public ResponseModel<ChangePasswordResult> resetPassword(UpdatePasswordRequest request);
	
	public ResponseModel<String> checkEmailExists(String email);
	
	public ResponseModel<User> updateStatus(String email);
	
	public ResponseModel<ConfirmForgotPasswordResult> updatePasswordWithVerificationCode(PasswordChangeRequest psRequest);
	
	public ResponseModel<Boolean> resendVerificationMail(String email);

}
