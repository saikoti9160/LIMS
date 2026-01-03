package com.digiworldexpo.lims.lab.request;

import java.sql.Timestamp;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.lab.dto.BranchMasterTimingsDTO;
import com.digiworldexpo.lims.lab.dto.PhlebotomistAvailabilityDTO;
import com.digiworldexpo.lims.lab.response.PhlebotomistMasterResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchMasterRequestDTO {
	
	   
	    private String branchName;
	    private UUID branchType;
	    private String phoneCode;
	    private String phoneNumber;
	    private String countryName;
	    private String stateName;
	    private String cityName;
	    private String pincode;
	    private String address;
	    private UUID roleId;
	    private String reportHeader;
	    private String reportFooter;
	    private String billHeader;
	    private String billFooter;
	    private String email;
	    private String password;
	   
	    private List<BranchMasterTimingsDTO> availabilities;

}
