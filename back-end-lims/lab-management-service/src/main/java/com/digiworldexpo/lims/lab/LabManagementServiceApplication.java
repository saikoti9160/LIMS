package com.digiworldexpo.lims.lab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.digiworldexpo.lims")
public class LabManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LabManagementServiceApplication.class, args);
	}

}
