package com.digiworldexpo.lims.lab.response;

import java.sql.Timestamp;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.master.BranchType;
import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.lab.dto.BranchMasterTimingsDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchMasterResponseDTO {
	
	    private UUID id;
	    private String branchName;
	    private BranchType branchType;
	    private String phoneCode;
	    private String phoneNumber;
	    private String countryName;
	    private String stateName;
	    private String cityName;
	    private String pincode;
	    private String address;
	    private Role role;
	    private String reportHeader;
	    private String reportFooter;
	    private String billHeader;
	    private String billFooter;
	    private String email;
	    private String password;
	    private String branchSequenceId;
	    private boolean active;
	    private UUID createdBy;	
	    private Timestamp createdOn;
	    private UUID modifiedBy;
	    private Timestamp modifiedOn;
	
	private List<BranchMasterTimingsDTO> availabilities;

}
