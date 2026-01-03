package com.digiworldexpo.lims.authentication.serviceimpl;

import java.util.HashMap;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.digiworldexpo.lims.authentication.constants.AuthConstants;
import com.digiworldexpo.lims.authentication.model.ResponseModel;
import com.digiworldexpo.lims.authentication.model.SignInResponse;
import com.digiworldexpo.lims.authentication.service.AuthService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class AuthServiceImpl implements AuthService{

	
	@Value("${aws.cognito.clientId}")
	private String clientId;

	@Value("${aws.cognito.userPoolId}")
	private String userPool;
	
	@Autowired
	private AWSCognitoIdentityProvider cognitoClient;
	
	@Override
	public ResponseModel<SignInResponse> getAuthTokenAfterExpiry(String refreshToken) {
		log.info("Begin AuthServiceImpl -> getAuthTokenAfterExpiry() method");
		ResponseModel<SignInResponse> responseModel;
	    try {
	        Map<String, String> authParams = new HashMap<>();
	        authParams.put(AuthConstants.REFRESH_TOKEN, refreshToken);
	        authParams.put(AuthConstants.CLIENT_ID, clientId);
	        authParams.put(AuthConstants.CLIENT_SECRET, "");

	        // Create and send the auth request
	        InitiateAuthRequest authRequest = new InitiateAuthRequest()
	                .withClientId(clientId)
	                .withAuthFlow(AuthFlowType.REFRESH_TOKEN)
	                .withAuthParameters(authParams);
	        InitiateAuthResult authResult = cognitoClient.initiateAuth(authRequest);

	        // Extract the authentication result
	        AuthenticationResultType authResultType = authResult.getAuthenticationResult();
	        SignInResponse signInResponse = new SignInResponse();
	        signInResponse.setAccessToken(authResultType.getAccessToken());
	        signInResponse.setIdToken(authResultType.getIdToken());
	        signInResponse.setRefreshToken(authResultType.getRefreshToken());
	        signInResponse.setExpiresIn(authResultType.getExpiresIn());
	        responseModel= createResponseModel(
	                HttpStatus.OK.toString(),
	                "Access token refreshed successfully.",
	                signInResponse
	        );
	    } catch (Exception e) {
	        log.error("Error refreshing access token: {}", e.getMessage());
	        responseModel=createResponseModel(
	                HttpStatus.INTERNAL_SERVER_ERROR.toString(),
	                "An error occurred while refreshing access token.",
	                null
	        );
	    }
	    log.info("Begin AuthServiceImpl -> getAuthTokenAfterExpiry() method");
	    return responseModel;
	}

	private <T> ResponseModel<T> createResponseModel(String statusCode, String message, T data) {
	    ResponseModel<T> responseModel = new ResponseModel<>();
	    responseModel.setStatusCode(statusCode);
	    responseModel.setMessage(message);
	    responseModel.setData(data);
	    responseModel.setTimestamp(String.valueOf(System.currentTimeMillis()));
	    return responseModel;
	}
	


}
