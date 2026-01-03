package com.digiworldexpo.lims.entities.lab_management;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="sample_mapping",schema = "lab")
public class SampleMapping extends BaseEntity{
    
    @OneToMany(mappedBy = "sampleMapping", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<TestConfigurationMaster> testConfigurations = new HashSet<>();
    
	   @Column(name = "test_name")
	    private String testName;

	    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
	    @JoinTable(
	        name = "sample_master_mapping",
	        schema = "lab",
	        joinColumns = @JoinColumn(name = "sample_mapping_id"),
	        inverseJoinColumns = @JoinColumn(name = "sample_master_id")
	    )
	    private Set<SampleMaster> sampleMasters = new HashSet<>();

	    @Column(name = "lab_id")
	    private UUID labId;
	       
	    private List<String> sampleTypes;
}
