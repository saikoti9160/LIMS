package com.digiworldexpo.lims.entities.master;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "designation", schema = "masterdata")
@Getter
@Setter	
@NoArgsConstructor
@AllArgsConstructor
public class Designation extends BaseEntity {

	@Column(name="designation_name")
	private String designationName;
}
