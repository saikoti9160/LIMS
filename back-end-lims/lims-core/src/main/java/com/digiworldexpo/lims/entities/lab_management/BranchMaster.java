package com.digiworldexpo.lims.entities.lab_management;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.digiworldexpo.lims.entities.master.BranchType;
import com.digiworldexpo.lims.entities.master.Cities;
import com.digiworldexpo.lims.entities.master.Countries;
import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.entities.master.States;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name="branch_master",schema="lab")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class BranchMaster extends BaseEntity{
	@Column(name = "branch_name",nullable = false)
    private String branchName;


    @ManyToOne
    @JoinColumn(name = "branch_type_id")
    private BranchType branchType;

    @Column(name = "phone_code")
    private String phoneCode;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "country")
    private String countryName;

    @Column(name = "state")
    private String stateName;

    @Column(name = "city")
    private String cityName;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "address")
    private String address;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

//    @Column(name = "branch_time")
//    private String branchTime;
    
    @OneToMany(mappedBy = "branchMaster", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference 
    private List<BranchMasterTimings> branchTime;
    
    @Column(name = "report_header")
    private String reportHeader;

    @Column(name = "report_footer")
    private String reportFooter;

    @Column(name = "bill_header")
    private String billHeader;

    @Column(name = "bill_footer")
    private String billFooter;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;
    
    @Column(name="branch_sequence_id")
    private String branchSequenceId;

}
