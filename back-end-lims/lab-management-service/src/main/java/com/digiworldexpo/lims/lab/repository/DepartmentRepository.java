package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.LabDepartment;

@Repository
public interface DepartmentRepository extends JpaRepository<LabDepartment, UUID> {
	

	    @Query("SELECT d FROM LabDepartment d WHERE LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
	           "AND d.createdBy = :createdBy AND d.active = :flag")
	    List<LabDepartment> findByNameAndCreatedByAndActive(
	        @Param("searchTerm") String searchTerm,
	        @Param("createdBy") UUID createdBy, 
	        @Param("flag") Boolean flag
	        );

	    List<LabDepartment> findAllByCreatedByAndActive(UUID createdBy, Boolean active);

}

