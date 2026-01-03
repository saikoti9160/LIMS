package com.digiworldexpo.lims.lab.request;

import java.util.UUID;

import com.digiworldexpo.lims.lab.dto.BillAccessConfigurationDTO;
import com.digiworldexpo.lims.lab.dto.PatientConfigurationDTO;
import com.digiworldexpo.lims.lab.dto.PaymentDetailsDTO;
import com.digiworldexpo.lims.lab.dto.ReportAccessConfigurationDTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationRequestDTO {
    private String name;
    private String phoneCode;
    private String phoneNumber;
    private String email;
    private String password;
    private String country;
    private String state;
    private String city;
    private String pinCode;
    private String address;

    private PaymentDetailsDTO paymentDetails; 
    
    private String comments;
    private String invoiceGenerationFrequency;
    private String customFrequency;

    private PatientConfigurationDTO patientConfiguration;
    private ReportAccessConfigurationDTO reportAccessConfiguration;
    private BillAccessConfigurationDTO billAccessConfiguration;
    
    private String modifiedBy;
    
    private UUID labId;
    private UUID roleId;
}
