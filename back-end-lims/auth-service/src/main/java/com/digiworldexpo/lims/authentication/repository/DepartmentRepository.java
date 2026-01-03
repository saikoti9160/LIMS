package com.digiworldexpo.lims.authentication.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID>{
	
	Optional<Department> findById(UUID id);
//	List<Department> findByCreatedByAndName(UUID createdBy, String startsWith);
//
//	List<Department> findAllByCreatedBy(UUID createdBy);
//
//	List<Department> findByCreatedBy(UUID createdBy, Pageable pageable);
//
//	Optional<Department> findByIdAndCreatedBy(UUID id, UUID createdBy);
//	
//	void deleteByIdAndCreatedBy(UUID id, UUID createdBy);
//
//	Optional<Department> findByName(String name);

}





