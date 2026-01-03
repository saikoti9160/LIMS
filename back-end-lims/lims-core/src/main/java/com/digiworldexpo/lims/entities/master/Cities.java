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
@Table(name = "cities", schema = "masterdata")
public class Cities extends BaseEntity {

	@Column(name = "state_code")
	private String stateCode;
	
	@Column(name = "state_name")
	private String stateName;
	
	@Column(name = "city_name")
	private String cityName;
}
