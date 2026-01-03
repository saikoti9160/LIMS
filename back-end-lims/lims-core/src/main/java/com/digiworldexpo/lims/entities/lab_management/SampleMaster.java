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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="sample_master", schema = "lab")
public class SampleMaster extends BaseEntity {

	@Column(name = "sample_name")
	private String sampleName;

	@Column(name = "sample_type")
	private List<String> sampleType;

	@Column(name = "lab_id")
	private UUID labId;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JsonIgnore
    private Set<SampleMapping> sampleMappings = new HashSet<>();

//	@ManyToMany(fetch = FetchType.LAZY)
//	@JsonIgnore
//	private Set<TestConfigurationMaster> testConfigurations = new HashSet<>();
}
