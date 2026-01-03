package com.digiworldexpo.lims.master.repository;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Designation;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, UUID> {
	
	Optional<Designation> findByDesignationName(String designationName);

	@Query("SELECT d FROM Designation d WHERE d.createdBy = :createdBy AND LOWER(d.designationName) = (:designationName)")
	Optional<Designation> findByDesignationNameAndCreatedBy(@Param("createdBy") UUID createdBy, @Param("designationName") String designationName);
	
	@Query("SELECT d FROM Designation d WHERE d.active=true AND d.createdBy = :createdBy AND LOWER(COALESCE(d.designationName,'')) LIKE LOWER(CONCAT('%', :startsWith, '%'))")
	List<Designation> findAllDesignationNamesWithCreatedByAndStartsWith(@Param("startsWith") String startsWith, @Param("createdBy") UUID createdBy, Sort sortedBy);
	
	@Query("SELECT d FROM Designation d WHERE d.active=true AND d.createdBy = :createdBy")
	List<Designation> findAllByActiveAndCreatedBy(@Param("createdBy") UUID createdBy, Sort sortedBy);
}
