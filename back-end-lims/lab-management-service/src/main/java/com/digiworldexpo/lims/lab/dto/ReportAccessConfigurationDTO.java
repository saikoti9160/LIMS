package com.digiworldexpo.lims.lab.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportAccessConfigurationDTO {
    private Boolean showHeader;
    private Boolean showFooter;
    private String headerText; 
    private String footerText;
}
