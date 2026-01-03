package com.digiworldexpo.lims.lab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientConfigurationDTO {
    private Boolean sendReportsAndBills;
    private Boolean sendReportsOnly;
}