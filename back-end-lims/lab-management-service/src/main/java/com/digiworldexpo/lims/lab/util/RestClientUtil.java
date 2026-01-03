package com.digiworldexpo.lims.lab.util;

import java.util.function.Supplier;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RestClientUtil {

	private final RestTemplate restTemplate;
	
	public RestClientUtil(RestTemplate restTemplate) {
		this.restTemplate=restTemplate;
	}
	
	  private static <T> T parse(String str, Class<T> responseClass) {
	        try {
	            if (str==null|| str.equals("") || responseClass == null) return null;
	            return new ObjectMapper().readValue(str, responseClass);
	        } catch (Exception ex) {
	        	log.info(ex.getMessage());
	            return null;
	        }
	    }
	
	 public <T, R> ResponseEntity<T> postForEntity(String url, R requestBody, Class<T> responseClass) {
	        return exec(() -> restTemplate.postForEntity(url, requestBody, responseClass), responseClass);
	 }
	 
	  private <T> ResponseEntity<T> exec(Supplier<ResponseEntity> supplier, Class<T> response) {
	        try {
	            return supplier.get();
	        } catch (HttpStatusCodeException httpException) {
	            log.error("HttpClientException: {}", httpException.getLocalizedMessage());
	            log.error("HttpStatusCodeException: ", httpException);
	            return ResponseEntity.status(httpException.getStatusCode()).body(parse(httpException.getResponseBodyAsString(), response));
	        } catch (Exception exception) {
	        	log.error("Exception: {}", exception.getLocalizedMessage());
	            return ResponseEntity.status(500).build();
	        }
	    }

}
