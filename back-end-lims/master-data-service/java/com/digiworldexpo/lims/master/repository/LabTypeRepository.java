package com.digiworldexpo.lims.master.repository;

import java.util.List;

import java.util.Optional;
import java.util.UUID;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.LabType;

@Repository
public interface LabTypeRepository extends JpaRepository<LabType, UUID>{
	
	  

	    Optional<LabType> findByCreatedByAndNameIgnoreCase(UUID createdBy, String name);
	    
	    @Query("SELECT l FROM LabType l WHERE l.createdBy = :createdBy AND LOWER(l.name) LIKE LOWER(CONCAT('%', :startsWith, '%'))")
	    Page<LabType> findByCreatedByAndNameStartingWith(@Param("createdBy") UUID createdBy, @Param("startsWith") String startsWith, Pageable pageable);

	    Page<LabType> findByCreatedBy(UUID createdBy, Pageable pageable);
	    
	    List<LabType> findByCreatedByAndName(UUID createdBy, String startsWith);

	    Optional<LabType> findByName(String name);

	    Optional<LabType> findById(UUID id);
}
