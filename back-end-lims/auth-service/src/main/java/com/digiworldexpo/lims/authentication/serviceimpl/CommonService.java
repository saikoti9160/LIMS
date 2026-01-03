package com.digiworldexpo.lims.authentication.serviceimpl;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.amazonaws.services.cognitoidp.model.AuthenticationResultType;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.digiworldexpo.lims.authentication.model.SignInResponse;
import com.digiworldexpo.lims.authentication.repository.UserAuthenticationRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CommonService {

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	private UserAuthenticationRepository userAuthenticationRepository;


	@Value("${from.email.address}")
	private String fromEmailAddress;

	public SignInResponse mapAuthToSignInResponse(InitiateAuthResult authResult, String email) {
		log.info("Begin CommonService -> mapAuthToSignInResponse() method");
		SignInResponse signInResponse = new SignInResponse();
		AuthenticationResultType authResultType = authResult.getAuthenticationResult();
		setAuthTokens(signInResponse, authResultType);
		signInResponse.setEmail(email);
		List<Object[]> userRoleAndAccountType = userAuthenticationRepository.findRoleAndAccountTypeEmail(email);

		if (Objects.nonNull(authResultType)) {
			signInResponse.setAccessToken(authResultType.getAccessToken());
			signInResponse.setIdToken(authResultType.getIdToken());
			signInResponse.setRefreshToken(authResultType.getRefreshToken());
			signInResponse.setExpiresIn(authResultType.getExpiresIn());
			signInResponse.setEmail(email);

			if (!userRoleAndAccountType.isEmpty()) {
				        mapUserDataToSignInResponse(signInResponse, userRoleAndAccountType.get(0));
			}
		}
		log.info("End CommonService -> mapAuthToSignInResponse() method");
		return signInResponse;
	}

	private void setAuthTokens(SignInResponse signInResponse, AuthenticationResultType authResultType) {
		log.info("Begin CommonService -> setAuthTokens() method");
		if (Objects.nonNull(authResultType)) {
			signInResponse.setAccessToken(authResultType.getAccessToken());
			signInResponse.setIdToken(authResultType.getIdToken());
			signInResponse.setRefreshToken(authResultType.getRefreshToken());
			signInResponse.setExpiresIn(authResultType.getExpiresIn());
		}
		log.info("End CommonService -> setAuthTokens() method");
	}

	private void mapUserDataToSignInResponse(SignInResponse signInResponse, Object[] userData) {
		setIfNotNull(signInResponse::setUserId, userData[0]);
		setIfNotNull(signInResponse::setUserName, userData[1]);
		setIfNotNull(signInResponse::setRoleName, userData[2]);
		setIfNotNull(signInResponse::setAccountType, userData[3]);
//		setIfNotNull(signInResponse::setPreferences, userData[4]);
	}

	private <T> void setIfNotNull(Consumer<T> setter, Object value) {
		if (value != null) {
			setter.accept((T) value);
		}
	}

	@Async
	public void sendEmail(final String reciepentMail, final String messageBody, final String subject) {
		log.info("Begin CommonService -> sendemail() method");
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");
		try {
			helper.setFrom(fromEmailAddress);
			helper.setTo(reciepentMail);
			helper.setSubject(subject);
			helper.setText(messageBody, true);
			mimeMessage.setFrom(fromEmailAddress);
			javaMailSender.send(mimeMessage);
			log.info("Mail is sent. {}", reciepentMail);
			log.info("End CommonService -> SendEmail() method");
		} catch (MessagingException e) {
			log.error("An error while sending mail "+e.getMessage() +" to "+ reciepentMail);
		}
	}

}
