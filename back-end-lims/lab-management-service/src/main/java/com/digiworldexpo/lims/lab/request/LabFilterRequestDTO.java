package com.digiworldexpo.lims.lab.request;

import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.Data;

@Data
public class LabFilterRequestDTO {
    private Timestamp startDate;
    private Timestamp endDate;
    private Boolean status;
    private String searchKey;
    private String country;
    private String continent;
    private String state;
    private String city;
   private String createdBy;
    

}
