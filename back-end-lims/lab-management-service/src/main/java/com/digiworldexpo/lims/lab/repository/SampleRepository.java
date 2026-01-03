package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digiworldexpo.lims.entities.lab_management.SampleMaster;

import jakarta.transaction.Transactional;

public interface SampleRepository extends JpaRepository<SampleMaster, UUID> {

	@Query("SELECT s FROM SampleMaster s WHERE "
            + "(:searchText IS NULL OR LOWER(s.sampleName) LIKE %:searchText%)")
    Page<SampleMaster> findSamplesWithSearchText(@Param("searchText") String searchText, Pageable pageable);


	Page<SampleMaster> findByLabId(UUID labId, Pageable pageable);

	    @Modifying
	    @Transactional
	    @Query(value = "DELETE FROM lab.sample_master_mapping WHERE sample_master_id = :sampleMasterId", nativeQuery = true)
	    void deleteSampleMappings(@Param("sampleMasterId") UUID sampleMasterId);


	    List<SampleMaster> findBySampleName(String sampleName);

}

