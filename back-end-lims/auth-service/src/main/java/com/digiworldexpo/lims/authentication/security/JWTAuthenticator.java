package com.digiworldexpo.lims.authentication.security;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.nimbusds.jwt.JWTClaimsSet;

public class JWTAuthenticator extends AbstractAuthenticationToken {

	private Serializable principal;

	private JWTClaimsSet claims;

	public JWTAuthenticator(Collection<? extends GrantedAuthority> authorities, Serializable principal,
			JWTClaimsSet claims) {
		super(authorities);
		this.claims = claims;
		this.principal = principal;
		super.setAuthenticated(true);

	}

	@Override
	public Serializable getCredentials() {
		return principal;
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}

}