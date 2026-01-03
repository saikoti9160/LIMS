package com.digiworldexpo.lims.authentication.serviceimpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ChangePasswordRequest;
import com.amazonaws.services.cognitoidp.model.ChangePasswordResult;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.ExpiredCodeException;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordRequest;
import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InvalidPasswordException;
import com.amazonaws.services.cognitoidp.model.NotAuthorizedException;
import com.amazonaws.services.cognitoidp.model.ResendConfirmationCodeRequest;
import com.amazonaws.services.cognitoidp.model.ResourceNotFoundException;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.amazonaws.services.cognitoidp.model.UserNotConfirmedException;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.amazonaws.services.cognitoidp.model.UsernameExistsException;
import com.digiworldexpo.lims.authentication.exceptions.AccountTypeNotFoundException;
import com.digiworldexpo.lims.authentication.exceptions.UserNotActiveException;
import com.digiworldexpo.lims.authentication.exceptions.ValueNotFoundException;
import com.digiworldexpo.lims.authentication.model.PasswordChangeRequest;
import com.digiworldexpo.lims.authentication.model.ResponseModel;
import com.digiworldexpo.lims.authentication.model.SignInResponse;
import com.digiworldexpo.lims.authentication.model.UpdatePasswordRequest;
import com.digiworldexpo.lims.authentication.model.UserModel;
import com.digiworldexpo.lims.authentication.repository.AccountRepository;
import com.digiworldexpo.lims.authentication.repository.DesignationRepository;
import com.digiworldexpo.lims.authentication.repository.LabRepository;
import com.digiworldexpo.lims.authentication.repository.RoleRepository;
import com.digiworldexpo.lims.authentication.repository.UserAuthenticationRepository;
import com.digiworldexpo.lims.authentication.service.UserAuthenticationService;
import com.digiworldexpo.lims.entities.User;
import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.master.Account;
import com.digiworldexpo.lims.entities.master.Designation;
import com.digiworldexpo.lims.entities.master.Role;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

	@Autowired
	UserAuthenticationRepository userAuthenticationRepository;

	@Value("${aws.cognito.clientId}")
	private String clientId;

	@Value("${aws.cognito.userPoolId}")
	private String userPool;

	@Autowired
	private AWSCognitoIdentityProvider cognitoClient;

	@Autowired
	private KmsService kmsService;

	@Autowired
	private CommonService commonService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private DesignationRepository designationRepository;
	
	@Autowired
	private LabRepository labRepository;


	@Override
	public ResponseModel<User> registerUserInCognito(UserModel userModel) {
		log.info("Begin UserAuthentication ServiceImpl -> registerUserInCognito() method");
		ResponseModel<User> responseModel;
		try {
			if (userModel.getAccountType() == null) {
				throw new AccountTypeNotFoundException("Provide a valid Account Type.");
			}

			List<Account> accounts = accountRepository.findAll();

			UUID accountType = userModel.getAccountType();

			boolean isAccountValid = accounts.stream().anyMatch(account -> account.getId().equals(accountType));

			if (!isAccountValid) {
				log.error("Invalid account type: {}", accountType);
				throw new AccountTypeNotFoundException("account type '" + accountType + "' not found.");
			}

			 log.info("Valid account type found. Proceeding with user email validation...");
			 log.info("email"+ userModel);
			validateExistingUser(userModel.getEmail());
			log.info("User email validation passed for email: {}", userModel.getEmail());

			String name = getFullName(userModel);
			
			log.info("Handling user password...");
			boolean isAutogeneratedPassword = handlePassword(userModel);
			log.info("Is autogenerated password: {}", isAutogeneratedPassword);

			log.info("Creating client metadata...");
			Map<String, String> clientMetaData = createClientMetaData(userModel);
			log.info("Creating user attributes...");
			List<AttributeType> userAttributes = createUserAttributes(userModel,name);

			log.info("Signing up user with Cognito...");
			SignUpResult signUpResult = signUpUser(userModel, userAttributes, clientMetaData);

			log.info(" End Cognito sign-up result: {}", signUpResult);
			if (isSignUpResultValid(signUpResult) && isAutogeneratedPassword) {
				sendTemporaryPasswordEmail(userModel);
			}
		
			
			User savedUser = saveUserToDatabase(userModel, signUpResult, isAutogeneratedPassword);
			saveUseronLab( userModel, savedUser);
			log.info("End registerUserInCognito() method...");
			
			responseModel= createResponseModel(HttpStatus.OK.toString(), "User registered successfully.", savedUser);
			

		} catch (UsernameExistsException usernameExistsException) {
			log.error("UsernameExistsException: {}", usernameExistsException.getMessage());
			responseModel= createResponseModel(HttpStatus.CONFLICT.toString(), usernameExistsException.getMessage(), null);
		} catch (UserNotConfirmedException userNotConfirmedException) {
			log.error("UserNotConfirmedException: {}", userNotConfirmedException.getMessage());
			responseModel= createResponseModel(HttpStatus.BAD_REQUEST.toString(), userNotConfirmedException.getMessage(), null);
		} catch (Exception exception) {
			log.error("Unexpected exception: {}", exception.getMessage());
			responseModel= createResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
					"Registration failed: " + exception.getMessage(), null);
		}
		log.info("End UserAuthentication ServiceImpl -> registerUserInCognito() method");
		return responseModel;
	}

	private void validateExistingUser(String email) {
	    log.info("Validating if the email '{}' is already registered.", email);
	    
	    if (email == null || email.trim().isEmpty()) {
	        throw new IllegalArgumentException("Email cannot be null or empty.");
	    }

	    Optional<User> userOptional = userAuthenticationRepository.findByEmail(email);

	    if (userOptional.isPresent()) {
	        User user = userOptional.get();
	        
	        if (!user.isEmailVerified()) {
	            log.warn("Email '{}' is registered but not verified.", email);
	            throw new UserNotConfirmedException("Provided email is not verified. Please verify to continue.");
	        } else {
	            log.warn("Duplicate registration attempt for email '{}'.", email);
	            throw new UsernameExistsException(
	                    "It appears that you are already registered with us. Please proceed to log in: " + email);
	        }
	    }
	}


	private boolean handlePassword(UserModel userModel) {
		log.info("Begin UserAuthentication ServiceImpl -> handlePassword() method");
		if (Objects.isNull(userModel.getPassword())) {
			String newPassword = KmsService.generateRandomPassword(10);
			userModel.setPassword(newPassword);
			log.info("End UserAuthentication ServiceImpl -> handlePassword() method");
			return true;
		}
		return false;
	}

	private Map<String, String> createClientMetaData(UserModel userModel) {
		 log.info("Begin createClientMetaData() method for user with email: {}", userModel.getEmail());
		Map<String, String> clientMetaData = new HashMap<>();
		clientMetaData.put("account Type", userModel.getAccountType().toString());
		 log.info("Client metadata created successfully for user with email: {}", userModel.getEmail());
		return clientMetaData;
	}
	
	
	private String getFullName(UserModel userModel) {
	    String firstName = Optional.ofNullable(userModel.getFirstName()).orElse("");
	    String lastName = Optional.ofNullable(userModel.getLastName()).orElse("");
	    return firstName + " " + lastName;
	}
	
	private List<AttributeType> createUserAttributes(UserModel userModel, String name) {
		
		log.info("Begin createUserAttributes() method for user with email: {}", userModel.getEmail());
		List<AttributeType> userAttributes = new ArrayList<>();

		if (Objects.nonNull(userModel.getEmail())) {
			userAttributes.add(new AttributeType().withName("email").withValue(userModel.getEmail()));
		}
		 
	    if (!name.isEmpty()) {
	    	  log.debug("Adding name attribute: {}", name);
	        userAttributes.add(new AttributeType().withName("name").withValue(name));
	    }
	    
	    log.info("User attributes created successfully for user with email: {}. Total attributes: {}", 
	             userModel.getEmail(), userAttributes.size());

	    return userAttributes;
	}

	private SignUpResult signUpUser(UserModel userModel, List<AttributeType> userAttributes,
			Map<String, String> clientMetaData) {
		 log.info("Preparing SignUpRequest for user: {}", userModel.getEmail());
		    SignUpRequest signUpRequest = new SignUpRequest()
		            .withClientId(clientId)
		            .withUsername(userModel.getEmail())
		            .withPassword(userModel.getPassword())
		            .withUserAttributes(userAttributes);
		    
		    SignUpResult signUpResult = cognitoClient.signUp(signUpRequest);
		    
		    log.info("Cognito sign-up result for user {}: {}", userModel.getEmail(), signUpResult);
		    
		    return signUpResult;
	}

	private boolean isSignUpResultValid(SignUpResult signUpResult) {
		return signUpResult != null && signUpResult.getUserSub() != null && signUpResult.getUserConfirmed() != null;
	}

	private void sendTemporaryPasswordEmail(UserModel userModel) {
		String subject = "Temporary Password";
		String body = "Your temporary password is: " + userModel.getPassword();
		log.info("sendTemporaryPasswordEmail : "+userModel.getEmail());
		commonService.sendEmail(userModel.getEmail(), body, subject);
	}
	
	
	public void saveUseronLab(UserModel userModel,User user) {
		if(!userModel.isSuperAdminCreated()) {
			Lab lab=new Lab();
			lab.setLabName(userModel.getLabName());
			lab.setEmail(userModel.getEmail());
			lab.setPhoneCode(userModel.getPhoneCode());
			lab.setPhoneNumber(userModel.getPhone());
			lab.setCountry(userModel.getCountry());
			lab.setState(userModel.getState());
			lab.setCity(userModel.getCity());
			lab.setAddress(userModel.getAddress());
			lab.setUserId(user.getId());
			lab.setCreatedBy(user.getId());

			Lab saveLab= labRepository.save(lab);
		}
		
	}

	private User saveUserToDatabase(UserModel userModel, SignUpResult signUpResult, boolean isAutogeneratedPassword)
			throws ValueNotFoundException {
		log.info("Preparing to save user details to the database for email: {}", userModel.getEmail());

		User userDetailsToDB = new User();
		userDetailsToDB.setEmail(userModel.getEmail());
		userDetailsToDB.setId(UUID.fromString(signUpResult.getUserSub()));
		userDetailsToDB.setEmailVerified(signUpResult.isUserConfirmed());
		userDetailsToDB.setFirstName(userModel.getFirstName()!=null ? userModel.getFirstName() : "");
		userDetailsToDB.setLastName(userModel.getLastName()!=null ? userModel.getLastName() : "");
		userDetailsToDB.setPhone(userModel.getPhone()!=null ? userModel.getPhone() : "");
		userDetailsToDB.setPhoneCode(userModel.getPhoneCode()!=null ? userModel.getPhoneCode() : "");
		userDetailsToDB.setCountry(userModel.getCountry()!=null ? userModel.getCountry() : "");
		userDetailsToDB.setState(userModel.getState()!=null ? userModel.getState() : "");
		userDetailsToDB.setCity(userModel.getCity()!=null ? userModel.getCity() : "");
		userDetailsToDB.setAddress(userModel.getAddress()!=null ? userModel.getAddress() : "");

		userDetailsToDB.setActive(true);
		userDetailsToDB.isAutoGeneratePassword();
		userDetailsToDB.setPassword(kmsService.encrypt(userModel.getPassword()));
		userDetailsToDB.setLabName(userModel.getLabName()!=null? userModel.getLabName(): "");

		
		// Handle designation
		if (userModel.getDesignation() != null) {
			Designation designation = designationRepository.findById(UUID.fromString(userModel.getDesignation().toString()))
				.orElseThrow(() -> new ValueNotFoundException("Designation not found"));
			userDetailsToDB.setDesignation(designation);
			log.info("Designation is :  " + designation.getDesignationName());
		} else {
			log.info("No designation provided.");
		}

		// Handle labName
		if (userModel.getLabId() != null) {
			Lab lab = labRepository.findById(userModel.getLabId())
				.orElseThrow(() -> new ValueNotFoundException("Lab not found"));
			userDetailsToDB.setLab(lab);
			log.info("LabName is :  " + lab.getId());
			
		} else {
			log.info("No labName provided.");
		}

		Account account = accountRepository.findById(UUID.fromString(userModel.getAccountType().toString()))
		        .orElseThrow(() -> new ValueNotFoundException("Account not found"));
		userDetailsToDB.setAccount(account);

		if (userModel.getRole() == null) {
			Optional<Role> role = roleRepository.findById(UUID.fromString("83d67f64-4525-4f67-8e75-5bd8c12ddd6c"));
			if (role.isPresent())
				userDetailsToDB.setRole(role.get());

			log.info("RoleType is :  " + role.get().getRoleName());
		} else {
			Role role = roleRepository.findById(UUID.fromString(userModel.getRole().toString()))
					.orElseThrow(() -> new ValueNotFoundException("role not found"));

			userDetailsToDB.setRole(role);

			log.info("RoleType is :  " + role.getRoleName());

		}
		log.info("End Preparing to save user details to the database for email: {}", userModel.getEmail());
		return userAuthenticationRepository.save(userDetailsToDB);
	}

	@Override
	public ResponseModel<SignInResponse> initiateLogin(String email, String password) {
		log.info("Begin UserAuthentication ServiceImpl -> initiateLogin() method");
		ResponseModel<SignInResponse> responseModel = new ResponseModel<>();
		responseModel.setTimestamp(String.valueOf(System.currentTimeMillis()));

		try {
			Optional<User> user = userAuthenticationRepository.findByEmail(email);
			if (user.isEmpty()) {
				log.info("User email not found: {}", email);
				throw new UserNotFoundException("Email not found.");
			}

			if (!user.get().isActive()) {
				log.info("User is not active: {}", email);
				throw new UserNotActiveException("Provided email ID is not Active.");
			}

			InitiateAuthRequest authRequest = new InitiateAuthRequest().withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
					.withClientId(clientId).addAuthParametersEntry("USERNAME", email)
					.addAuthParametersEntry("PASSWORD", password);
			SignInResponse signInResponse = commonService
					.mapAuthToSignInResponse(cognitoClient.initiateAuth(authRequest), email);
			log.info("End UserAuthentication ServiceImpl -> initiateLogin() method");
			responseModel=createResponseModel(HttpStatus.OK.toString(), "User logged in Successfully.", signInResponse);
		} catch (UserNotFoundException e) {
			log.error("User not found: {}", e.getMessage());
			responseModel=createResponseModel(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null);
		} catch (UserNotActiveException e) {
			log.error("User not active: {}", e.getMessage());
			responseModel=createResponseModel(HttpStatus.FORBIDDEN.toString(),e.getMessage() , null);
		} catch (NotAuthorizedException e) {
			log.error("Not authorized: {}", e.getMessage());
			responseModel=createResponseModel(HttpStatus.UNAUTHORIZED.toString(), "Invalid credentials, please try again.", null);
		} catch (UserNotConfirmedException e) {
			log.error("User not confirmed: {}", e.getMessage());
			responseModel=createResponseModel(HttpStatus.FORBIDDEN.toString(), "Confirm your account by clicking on the verification link sent to your email.", null);
		} catch (Exception e) {
			log.error("Unexpected error during login: {}", e.getMessage());
			responseModel=createResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An Unexpected error occurred during login", null);
		}
		
		return responseModel;
	}

	@Override
	public ResponseModel<ForgotPasswordResult> forgotPassword(String email) {
		log.info("Begin UserAuthentication ServiceImpl -> forgotPassword() method");
		try {
			// Fetch user from the database
			User userFromDb = userAuthenticationRepository.findByEmail(email).orElseThrow(
					() -> new ValueNotFoundException("User Details Not Found With Provided Email: " + email));

			log.info("Fetched user details from the database for updating password: {}", userFromDb.getEmail());

			// Prepare the forgot password request
			ForgotPasswordRequest forgotPasswordRequest = new ForgotPasswordRequest().withClientId(clientId)
					.withUsername(email);

			// Call Cognito client for forgot password process
			ForgotPasswordResult forgotPasswordResult = cognitoClient.forgotPassword(forgotPasswordRequest);

			// Prepare and return the ResponseModel
			log.info("End UserAuthentication ServiceImpl -> forgotPassword() method");
			return createResponseModel(HttpStatus.OK.toString(), "Forgot password process initiated successfully.", forgotPasswordResult);

		} catch (ValueNotFoundException valueNotFoundException) {
			log.error("User not found for email: {}", email);
			return createResponseModel(HttpStatus.NOT_FOUND.toString(), valueNotFoundException.getMessage(), null);
		} catch (Exception exception) {
			log.error("Forgot password process failed: {}", exception.getMessage());
			return createResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Forgot password process could not be initiated.", null);
		}
	}

	// Helper method to create a ResponseModel
	private <T> ResponseModel<T> createResponseModel(String statusCode, String message, T data) {
		ResponseModel<T> responseModel = new ResponseModel<>();
		responseModel.setStatusCode(statusCode);
		responseModel.setMessage(message);
		responseModel.setData(data);
		responseModel.setTimestamp(String.valueOf(System.currentTimeMillis()));
		return responseModel;
	}

	@Override
	public ResponseModel<ChangePasswordResult> resetPassword(UpdatePasswordRequest request) {
		log.info("Begin UserAuthentication ServiceImpl -> resetPassword() method");
		try {
			// Verify if the user exists in the database
			userAuthenticationRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ValueNotFoundException(
					"User Details Not Found With Provided Email: " + request.getEmail()));

			log.info("Fetched user details from the database for updating password: {}", request.getEmail());

			// Create the ChangePasswordRequest
			ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest()
					.withPreviousPassword(request.getPassword()) // Assuming decryptData() decrypts the stored password
					.withProposedPassword(request.getNewPassword()).withAccessToken(request.getToken());

			// Call Cognito to change the password
			ChangePasswordResult changePasswordResult = cognitoClient.changePassword(changePasswordRequest);

			log.info("End UserAuthentication ServiceImpl -> resetPassword() method");
			return createResponseModel(HttpStatus.OK.toString(), "Password reset successfully.", changePasswordResult);

		} catch (ValueNotFoundException e) {
			log.error("ValueNotFoundException: {}", e.getMessage());
			return createResponseModel(HttpStatus.NOT_FOUND.toString(), e.getMessage(), null);
		} catch (InvalidPasswordException e) {
			log.error("InvalidPasswordException: {}", e.getMessage());
			return createResponseModel(HttpStatus.BAD_REQUEST.toString(), "Password did not conform with policy.", null);
		} catch (ResourceNotFoundException e) {
			log.error("ResourceNotFoundException: {}", e.getMessage());
			return createResponseModel(HttpStatus.BAD_REQUEST.toString(), "Invalid password format.", null);
		} catch (NotAuthorizedException e) {
			log.error("NotAuthorizedException: {}", e.getMessage());
			return createResponseModel(HttpStatus.NOT_FOUND.toString(), "Incorrect username or password.", null);
		} catch (Exception e) {
			log.error("Exception: {}", e.getMessage());
			return createResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "An error occurred while resetting the password.", null);
		}
	}


		@Override
		public ResponseModel<String> checkEmailExists(String email) {
			log.info("Checking email existence: {}", email);
	
			try {
				boolean isExist = userAuthenticationRepository.existsByEmail(email) || isUserInCognito(email);
	
				String status = isExist ? HttpStatus.CONFLICT.toString() : HttpStatus.OK.toString();
				String message = isExist ? "Email already exists." : "Email is available.";
	
				return createResponseModel(status, message, message);
			} catch (Exception e) {
				log.error("Error checking email existence: {}", e.getMessage());
				return createResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
						"An unexpected error occurred while checking email existence.", null);
			}
		}
	
		private boolean isUserInCognito(String email) {
			try {
				cognitoClient.adminGetUser(new AdminGetUserRequest().withUserPoolId(userPool).withUsername(email));
				log.info("User exists in Cognito: {}", email);
				return true;
			} catch (UserNotFoundException e) {
				log.warn("User not found in Cognito: {}", email);
				return false;
			}
		}

	@Override
	public ResponseModel<User> updateStatus(String email) {
		log.info("Begin UserAuthentication ServiceImpl -> registerUserInCognito() method");
		Optional<User> user = userAuthenticationRepository.findByEmail(email);
		ResponseModel<User> responseModel = new ResponseModel<>();
		responseModel.setTimestamp(String.valueOf(System.currentTimeMillis()));
		if (user.isPresent()) {
			user.get().setEmailVerified(true);

			User savedUser = userAuthenticationRepository.save(user.get());
			log.info("User status updated successfully for user: {}", email);
			responseModel=createResponseModel(HttpStatus.OK.toString(), "User Status Updated Successfully", savedUser);
			return responseModel;
		}
		log.error("user not found with this email : "+ email);
		responseModel=createResponseModel(HttpStatus.NOT_FOUND.toString(), "User Not Found", null);
		log.info("End UserAuthentication ServiceImpl -> updateStatus() method");
		return responseModel;
	}

	@Override
	public ResponseModel<ConfirmForgotPasswordResult> updatePasswordWithVerificationCode(
			PasswordChangeRequest psRequest) {
		log.info("Begin UserAuthentication ServiceImpl -> updatePasswordWithVerificationCode() method");
		try {
			ConfirmForgotPasswordRequest confirmForgotPasswordRequest = new ConfirmForgotPasswordRequest()
					.withClientId(clientId).withUsername(psRequest.getEmail())
					.withConfirmationCode(psRequest.getVerificationCode()).withPassword(psRequest.getNewPassword());
			// Call Cognito client for updating the password
			ConfirmForgotPasswordResult confirmForgotPasswordResult = cognitoClient
					.confirmForgotPassword(confirmForgotPasswordRequest);
			// Prepare and return the ResponseModel
			log.info("Password changed successfully");
			log.info("End UserAuthentication ServiceImpl -> updatePasswordWithVerificationCode() method");
			return createResponseModel(HttpStatus.OK.toString(), "Password changed successfully.",
					confirmForgotPasswordResult);
		} catch (ExpiredCodeException expiredCodeException) {
			log.error("Verification code expired for email: {}", psRequest.getEmail());
			return createResponseModel(HttpStatus.NOT_FOUND.toString(),
					"Verification code expired. Please regenerate the reset link.", null);
		} catch (Exception exception) {
			log.error("Password reset failed for email: {}", psRequest.getEmail());
			return createResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.toString(), "Password reset process failed.",
					null);
		}
	}

	@Override
	public ResponseModel<Boolean> resendVerificationMail(String email) {
		log.info("Begin UserAuthentication ServiceImpl -> resendVerificationMail() method");
		try {
			// Create and send the Cognito request
			ResendConfirmationCodeRequest resendRequest = new ResendConfirmationCodeRequest().withClientId(clientId)
					.withUsername(email);
			cognitoClient.resendConfirmationCode(resendRequest);

			// Check if the user exists in the database
			Optional<User> user = userAuthenticationRepository.findByEmail(email);
			if (user.isPresent()) {
				User existingUser = user.get();

				// If the password is auto-generated, send the temporary password email
				if (existingUser.isAutoGeneratePassword()) {
					String decryptedPassword = kmsService.decrypt(existingUser.getPassword());
					String subject = "Your Temporary Password";
					String body = "Your temporary password is: " + decryptedPassword;
					commonService.sendEmail(email, body, subject);
				}
				log.info("verification mail sended successfully to email : " +email);
				log.info("End UserAuthentication ServiceImpl -> resendVerificationMail() method");
				return createResponseModel(HttpStatus.OK.toString(), "Verification email resent successfully.", true);
			} else {
				log.error("user not found to resndVerificationMail with email : "+email);
				return createResponseModel(HttpStatus.NOT_FOUND.toString(), "User not found with provided email.",
						false);
			}
		} catch (Exception e) {
			log.error("Error resending verification email: {}", e.getMessage());
			return createResponseModel(HttpStatus.INTERNAL_SERVER_ERROR.toString(),
					"Error resending verification email: " + e.getMessage(), false);
		}
	}

}
