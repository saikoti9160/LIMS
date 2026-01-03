package com.digiworldexpo.lims.lab.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digiworldexpo.lims.entities.lab_management.SampleMapping;
public interface SampleMappingRepository extends JpaRepository<SampleMapping, UUID> {
    
	
		 @Query("SELECT sm FROM SampleMapping sm WHERE sm.labId = :labId ORDER BY sm.testName ASC")
		    Page<SampleMapping> findByLabId(@Param("labId") UUID labId, Pageable pageable);

		    // Fetch SampleMappings by testName (case-insensitive search)
		    @Query("SELECT sm FROM SampleMapping sm WHERE LOWER(sm.testName) LIKE LOWER(CONCAT('%', :searchText, '%')) ORDER BY sm.testName ASC")
		    Page<SampleMapping> findByTestName(@Param("searchText") String searchText, Pageable pageable);

		    // Fetch SampleMappings associated with SampleMasters having a matching sampleName (case-insensitive)
		    @Query("SELECT sm FROM SampleMapping sm " +
		           "JOIN sm.sampleMasters smm " +  // Ensures proper aliasing
		           "WHERE LOWER(smm.sampleName) LIKE LOWER(CONCAT('%', :sampleName, '%')) " +
		           "ORDER BY sm.testName ASC")  // Ordering by sm.testName for consistency
		    Page<SampleMapping> findBySampleName(@Param("sampleName") String sampleName, Pageable pageable);
		}