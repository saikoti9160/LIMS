package com.digiworldexpo.lims.lab.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentMasterSearchResponse {
	
	   private UUID id;
	   private String departmentName;

}
