package com.digiworldexpo.lims.entities.lab_management;


import java.sql.Timestamp;
import java.util.Set;
import java.util.UUID;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lab_equipment",schema = "lab")
@NoArgsConstructor
@AllArgsConstructor 
@Getter
@Setter
public class LabEquipment extends BaseEntity { 
	
    @Column(name = "equipment_name", nullable = false)
    private String equipmentName; 

    @Column(name = "equipment_type")
    private String equipmentType; 

    @Column(name = "manufacture")
    private String manufacture; 

    @Column(name = "model_number")
    private String modelNumber;  

    @Column(name = "serial_number")
    private String serialNumber;  

    @Column(name = "purchase_date")
    private Timestamp purchaseDate;  

    @Column(name = "installation_date")
    private Timestamp installationDate;  

//
//	@OneToMany(cascade = CascadeType.ALL,mappedBy = "equipment")
////	@JsonManagedReference
//    private Set<TestConfigurationMaster> tests;  
//
//	@OneToMany(cascade = CascadeType.ALL,mappedBy = "equipment")
////	@JsonManagedReference
//    private Set<TestParameter> testParameters;

    @Column(name = "integration_type")
    private String integrationType;  

    @Column(name = "communication_protocol")
    private String communicationProtocol;  

    @Column(name = "data_transfer_method")
    private String dataTransferMethod;  

    @Column(name = "result_format")
    private String resultFormat;  

    @Column(name = "ip_address")
    private String ipAddress;  

    @Column(name = "port")
    private Integer port;  

    @Column(name = "driver_details")
    private String driverDetails;  

    @Column(name = "api_credentials")
    private String apiCredentials;  

    @Column(name = "connection_status")
    private String connectionStatus;  

    @Column(name = "lab_id")
    private UUID labId;
	
} 