package com.digiworldexpo.lims.lab.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignatureMasterRequestDTO {
	
	private UUID id;
	private String signerName;
	private String uploadSignature;

	private UUID labId;
	private boolean active;
	
}
