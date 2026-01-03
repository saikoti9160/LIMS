package com.digiworldexpo.lims.inventory.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.digiworldexpo.lims.entities.MasterData;
import com.digiworldexpo.lims.entities.User;

@RestController
public class MasterController {

	private User user = new User();
	private MasterData master;

	@GetMapping(value = "/api/user", produces = "application/json")
	public User getEntities() {
		
		user.setUserName("asdf");
		return user;

	}

}
