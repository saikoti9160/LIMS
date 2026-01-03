package com.digiworldexpo.lims.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.digiworldexpo.lims")
public class LimsAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LimsAuthServiceApplication.class, args);
	}

}
