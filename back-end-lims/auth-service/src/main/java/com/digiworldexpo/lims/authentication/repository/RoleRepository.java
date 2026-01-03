package com.digiworldexpo.lims.authentication.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

//	Role findByRoleName(UUID role);
	

}
