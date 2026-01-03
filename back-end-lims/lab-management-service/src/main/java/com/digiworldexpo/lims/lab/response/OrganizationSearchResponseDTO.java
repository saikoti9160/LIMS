package com.digiworldexpo.lims.lab.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationSearchResponseDTO {
	
	private UUID id;
	 private String organizationSequenceId;
	 private String name;
	 private String phoneNumber;
	 private String email;

}
