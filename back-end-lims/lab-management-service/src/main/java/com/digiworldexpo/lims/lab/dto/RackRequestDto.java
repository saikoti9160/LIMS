package com.digiworldexpo.lims.lab.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class RackRequestDto {

	private UUID id;
	private UUID labId;
	private String rackNumber;
	private Integer rows;
	private Integer columns;
	private boolean active;
}