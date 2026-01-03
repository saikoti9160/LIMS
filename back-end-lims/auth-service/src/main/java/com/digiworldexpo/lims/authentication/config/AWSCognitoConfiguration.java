package com.digiworldexpo.lims.authentication.config;

import java.net.MalformedURLException;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.kms.AWSKMS;
import com.amazonaws.services.kms.AWSKMSClientBuilder;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

@Configuration
public class AWSCognitoConfiguration {

	@Value("${aws.cognito.connectionTimeOut}")
	private int connectionTimeOut;

	@Value("${aws.cognito.readTimeout}")
	private int readTimeout;

	@Value("${aws.cognito.jwkUrl}")
	private String jwkUrl;
	
	@Value("${aws.accessKeyId}")
	private String accessKey;
	
	@Value("${aws.secretKey}")
	private String secretKey;

	@Bean
	public BasicAWSCredentials basicAWSCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	@Bean
	public ConfigurableJWTProcessor configurableJWTprocessor() throws MalformedURLException {
		ResourceRetriever resourceRetriver = new DefaultResourceRetriever(connectionTimeOut, readTimeout);
		URL jwkURL = new URL(jwkUrl);
		JWKSource jwkSource = new RemoteJWKSet(jwkURL, resourceRetriver);
		ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
		JWSVerificationKeySelector keySelector = new JWSVerificationKeySelector(JWSAlgorithm.RS256, jwkSource);
		jwtProcessor.setJWSKeySelector(keySelector);
		return jwtProcessor;
	}
	
	@Bean
	public AWSKMS getAWSKMS() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
	    return AWSKMSClientBuilder.standard()
	    		.withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
	            .withRegion(Regions.AP_SOUTH_1)
	            .build();
	}

	@Bean
	public AWSCognitoIdentityProvider awsCognitoIdentityProvider(BasicAWSCredentials basicAWSCredentials) {
		return AWSCognitoIdentityProviderClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(basicAWSCredentials))
				.withRegion(Regions.AP_SOUTH_1)
				.build();
	}



}
