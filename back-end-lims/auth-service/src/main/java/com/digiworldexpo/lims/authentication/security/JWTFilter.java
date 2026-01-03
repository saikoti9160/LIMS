package com.digiworldexpo.lims.authentication.security;

import java.io.IOException;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JWTFilter extends OncePerRequestFilter {

	@Autowired
	JWTProvider jwtProvider;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Authentication authentication;
		try {
			authentication = jwtProvider.authenticate(request);
			if (Objects.nonNull(authentication))
				SecurityContextHolder.getContext().setAuthentication(authentication);
		} catch (Exception e) {
		}
		filterChain.doFilter(request, response);
	}

}
