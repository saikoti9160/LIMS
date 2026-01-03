package com.digiworldexpo.lims.entities.master;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "states", schema = "masterdata")
public class States extends BaseEntity{

	@Column(name = "state_name")
	private String stateName;
	
	@Column(name = "state_code")
	private String stateCode;
	
	@Column(name = "country_name")
	private String countryName;
	
	@Column(name = "country_code")
	private String countryCode;
}
