package com.digiworldexpo.lims.entities.lab_management;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="branch",schema="lab")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Branch  extends BaseEntity{

	@Column(name="branch_name")
    private String branchName;
	
	@Column(name="branch_type",  nullable = false)
    private String branchType; 
	
	@Column(name="contact_person",  nullable = false)
    private String contactPerson; 
	
	@Column(name="email", nullable = false)
    private String email;
	
	@Column(name="phone_number", nullable = false)
    private String phoneNumber; 
	
	@Column(name="continent",  nullable = false)
    private String continent;
	
	@Column(name="country", nullable = false)
    private String country;
	
	@Column(name="state",  nullable = false)
    private String state;
	
	@Column(name="city",  nullable = false)
    private String city;
	
	@Column(name="address", nullable = false)
    private String address;
	
	@Column(name = "zip_code",nullable = false)
	private String zipCode;
     
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_id", referencedColumnName = "id")
    private Lab lab; 
}
