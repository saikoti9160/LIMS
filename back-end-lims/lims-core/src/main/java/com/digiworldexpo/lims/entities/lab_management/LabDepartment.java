package com.digiworldexpo.lims.entities.lab_management;

import java.util.Set;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "lab_department", schema = "lab")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LabDepartment extends BaseEntity {
	
	@Column(name = "department_name")
	private String departmentName;
	
	@ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "lab_id", referencedColumnName = "id")
	@JsonIgnore
    private Lab lab;
	

	 	@OneToMany(mappedBy = "labDepartment", fetch = FetchType.LAZY)
	 	@JsonIgnore
	    private Set<TestConfigurationMaster> testConfigurations;

	
}

