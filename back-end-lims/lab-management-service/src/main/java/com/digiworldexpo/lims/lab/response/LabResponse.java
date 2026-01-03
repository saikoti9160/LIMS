package com.digiworldexpo.lims.lab.response;
import java.sql.Timestamp;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabResponse {
	
    private UUID id;
    private String labName;
    private String email;
    private Timestamp createdOn;
    private String country;
    private boolean active;
   private String logo;
   private  String  createdBy;
}
