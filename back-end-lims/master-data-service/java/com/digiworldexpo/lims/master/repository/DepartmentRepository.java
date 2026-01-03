package com.digiworldexpo.lims.master.repository;

import java.util.List;


import java.util.Optional;
import java.util.UUID;


import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Department;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

	  @Query("SELECT d FROM Department d WHERE d.createdBy = :createdBy AND LOWER(d.name) LIKE LOWER(CONCAT('%', :startsWith, '%'))")
	    Page<Department> findByCreatedByAndNameContaining(@Param("createdBy") UUID createdBy,
	                                                     @Param("startsWith") String startsWith, Pageable pageable);

	Optional<Department> findByCreatedByAndNameIgnoreCase(UUID createdBy, String name);
	
	Page<Department> findByCreatedByAndNameStartingWith(UUID createdBy, String startsWith, Pageable pageable);

	Page<Department> findByCreatedBy(UUID createdBy, Pageable pageable);

	List<Department> findByCreatedByAndName(UUID createdBy, String startsWith);

	Optional<Department> findByName(String name);
	
	long countByCreatedBy(UUID createdBy);
	

}