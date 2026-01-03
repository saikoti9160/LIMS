package com.digiworldexpo.lims.entities.lab_management;

import java.util.List;
import java.util.UUID;
import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "lab",schema = "lab")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Lab extends BaseEntity {

	
	@Column(name="lab_manager_name")
    private String labManagerName; 
	
	@Column(name="lab_name")
    private String labName; 
	
	@Column(name="lab_type_id")
    private UUID labTypeId;
	
	@Column(name="email")
    private String email; 
	
	@Column(name="continent")
    private String continent; 
	
	@Column(name="country")
    private String country;
	
	@Column(name="state")
    private String state;
	
	@Column(name="city")
    private String city;
	
	@Column(name="address")
    private String address;
	
	@Column(name="zip_code")
    private String zipCode;
	
	@Column(name="phone_number")
    private String phoneNumber;
	
	@Column(name="phone_code")
    private String phoneCode;
	
	@Column(name="package_id")
    private UUID packageId; 
	
	@Column(name="has_branches")
    private boolean hasBranches;
	
	@Column(name="logo")
    private String logo; 
	
    @Column(name = "user_id")
    private UUID userId;
 
	
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "lab", orphanRemoval = true,cascade = CascadeType.ALL)
    private List<Branch> branches; 
 
	
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "lab", orphanRemoval = true,cascade = CascadeType.ALL) 
    private List<Equipment> equipmentList; 
}
