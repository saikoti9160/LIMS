package com.digiworldexpo.lims.lab.response;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorSearchResponseDTO {

    private UUID id;
    private String doctorSequenceId;
    private String doctorName;
    private String departmentName;
    private String email;
    private Boolean isReportApprover;
}
