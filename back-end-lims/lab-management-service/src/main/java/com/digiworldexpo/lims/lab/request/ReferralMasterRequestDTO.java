package com.digiworldexpo.lims.lab.request;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferralMasterRequestDTO {
	 private String referralName;
	    private String phoneNumber;
	    private String dateOfBirth;
	    private UUID roleId;
	    private UUID labId;
	    private String email;
	    private String password;
	    private boolean active;
	    

}
