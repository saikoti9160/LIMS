package com.digiworldexpo.lims.entities.lab_management;
import java.util.UUID;

import com.digiworldexpo.lims.entities.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name="rack",schema = "lab")
public class Rack extends BaseEntity{

	@Column(name = "rack_number")
	private String rackNumber;

	@Column(name = "rows")
	private Integer rows;

	@Column(name = "columns")
	private Integer columns;

	@Column(name = "lab_id")
	private UUID labId;
}
