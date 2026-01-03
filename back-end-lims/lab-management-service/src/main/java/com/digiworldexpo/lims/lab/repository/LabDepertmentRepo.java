package com.digiworldexpo.lims.lab.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.LabDepartment;
@Repository
public interface LabDepertmentRepo extends JpaRepository<LabDepartment, UUID> {
	
	@Query("SELECT d FROM LabDepartment d WHERE LOWER(d.departmentName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	Page<LabDepartment> findByName(@Param("searchTerm") String searchTerm, Pageable pageable);
}
