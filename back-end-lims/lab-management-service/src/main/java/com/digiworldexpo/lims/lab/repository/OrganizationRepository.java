package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digiworldexpo.lims.entities.lab_management.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
	
	 @Query(value = "SELECT LPAD(CAST(nextval('organization_sequence_id') AS TEXT), 4, '0')", nativeQuery = true)
	    String getNextFormattedOrganizationSequenceId();
	 
	 @Query("SELECT o FROM Organization o " +
		       "WHERE o.createdBy = :createdBy " +
		       "AND o.active = :flag " +
		       "AND (:keyword IS NULL OR :keyword = '' OR " +
		       "LOWER(o.organizationSequenceId) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "OR LOWER(o.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "OR LOWER(o.phoneNumber) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "OR LOWER(o.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
		List<Organization> findByCreatedByAndActiveAndKeyword(
		        @Param("createdBy") UUID createdBy,
		        @Param("flag") Boolean flag,
		        @Param("keyword") String keyword);

	 
	 

	 List<Organization> findAllByCreatedByAndActive(UUID createdBy, boolean active);

	    
	    

}
