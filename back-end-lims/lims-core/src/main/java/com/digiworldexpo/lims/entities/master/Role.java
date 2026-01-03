package com.digiworldexpo.lims.entities.master;

import java.util.List;
import java.util.UUID;

import com.digiworldexpo.lims.entities.BaseEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;

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

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "role", schema = "masterdata")
public class Role extends BaseEntity {

	@Column(name = "role_name")
	private String roleName;
	

	@OneToMany(mappedBy = "role",cascade = CascadeType.ALL,fetch = FetchType.EAGER )
	@JsonManagedReference
	private List<Permission> permission;
	
	@Column(name = "lab_id")
	private UUID lab;
}
