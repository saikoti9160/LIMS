package com.digiworldexpo.lims.lab.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OutsourceSearchResponse {
	
	private UUID id;
	private String outsourceCenterName;
	private String outsourceSequenceId;
	private String email;
	private boolean active;

}
