package com.digiworldexpo.lims.lab.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LabMainResponse {

    private List<LabResponse> lab;
    private Integer totalLabs;
    private Integer activeLabs;
    private Integer totalinactivelabs;
    private Long registeredLabs;
    private Integer expiringLabs;


}
