package com.digiworldexpo.lims.authentication.service;

import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.authentication.model.ResponseModel;
import com.digiworldexpo.lims.authentication.model.SignInResponse;



@Service
public interface AuthService {

	ResponseModel<SignInResponse> getAuthTokenAfterExpiry(String token) throws Exception;
}
