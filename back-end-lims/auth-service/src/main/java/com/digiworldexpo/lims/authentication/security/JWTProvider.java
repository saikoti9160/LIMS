package com.digiworldexpo.lims.authentication.security;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Component;

import com.digiworldexpo.lims.authentication.constants.AuthConstants;
import com.digiworldexpo.lims.authentication.exceptions.UserNameNotFoundException;
import com.digiworldexpo.lims.authentication.repository.UserAuthenticationRepository;
import org.springframework.security.core.userdetails.User;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTProvider {
	@Value("${aws.cognito.identityPoolUrl}")
	private String idenityPoolURl;

	ConfigurableJWTProcessor configurableJWTProcessor;
	

	UserAuthenticationRepository userAuthenticationRepository;
 
	public JWTProvider(ConfigurableJWTProcessor configurableJWTProcessor,
			UserAuthenticationRepository userAuthenticationRepository) {
		super();
		this.configurableJWTProcessor = configurableJWTProcessor;
		this.userAuthenticationRepository = userAuthenticationRepository;
	}

	private String getUserName(JWTClaimsSet claims) {
		return claims.getClaim(AuthConstants.USERNAME).toString();
	}

	private void validateToken(JWTClaimsSet claims) throws Exception {
		if (!claims.getIssuer().equals(idenityPoolURl)) {
			throw new Exception("JWT issuer is not valid");
		}
		Date now = new Date();
		if (claims.getExpirationTime().before(now)) {
			throw new Exception("JWT has expired");
		}
	}

	public Authentication authenticate(HttpServletRequest request) throws Exception {
		String header = request.getHeader(AuthConstants.AUTHORIZATION);
		String token = getToken(header);
		JWTClaimsSet claims = configurableJWTProcessor.process(token, null);
		validateToken(claims);
		String userID = getUserName(claims);
		if (Strings.isNotBlank(userID)) {
//			List<String> userDetails = userRespository.findPrivilegesById(UUID.fromString(userID));
//			List<SimpleGrantedAuthority> authorities = userDetails.stream().map(SimpleGrantedAuthority::new)
//					.collect(Collectors.toList());
			Set<SimpleGrantedAuthority> authorities = new HashSet<>();
			authorities.add(new SimpleGrantedAuthority("EDIT_PROFILE"));
			User user = new User(userID, "", authorities);
			return new JWTAuthenticator(authorities, user, claims);
		}
		throw new UserNameNotFoundException("Username not Found in the token");
	}

	private String getToken(String token) {
		return token.startsWith(AuthConstants.BEARER) ? token.substring(AuthConstants.BEARER.length()) : token;
	}

}