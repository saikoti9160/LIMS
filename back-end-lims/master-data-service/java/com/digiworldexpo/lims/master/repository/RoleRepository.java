package com.digiworldexpo.lims.master.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

	@Query("SELECT r FROM Role r WHERE r.createdBy = :createdBy AND LOWER(r.roleName) = (:roleName)")
	Optional<Role> findByRoleNameAndCreatedBy(@Param("createdBy") UUID createdBy, @Param("roleName") String roleName);

	@Query("SELECT r FROM Role r where LOWER(COALESCE(r.roleName,'')) LIKE LOWER(CONCAT('%',:startsWith,'%')) AND (:status IS NULL OR r.active=:status)")
	List<Role> getAllRolesByParams(@Param("startsWith") String startsWith, @Param("status") Boolean status,
			Sort sortedBy);

	@Query("SELECT r FROM Role r where  (:status IS NULL OR r.active=:status)")
	List<Role> findAllAndActive(@Param("status") Boolean status, Sort sortedBy);
}
