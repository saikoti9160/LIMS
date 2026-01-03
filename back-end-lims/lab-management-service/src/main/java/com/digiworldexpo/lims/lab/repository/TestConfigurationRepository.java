package com.digiworldexpo.lims.lab.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.TestConfigurationMaster;

@Repository
public interface TestConfigurationRepository extends JpaRepository<TestConfigurationMaster, UUID>{

//	@Query("SELECT s FROM SampleMapping s WHERE "
//            + "(:searchText IS NULL OR LOWER(s.testName) LIKE %:searchText%)")
//	Page<SampleMapping> findByTestName(String searchText, Pageable pageable);
//
//	   @Query("SELECT t FROM TestConfigurationMaster t WHERE t.labId = :labId")
//	Page<TestConfigurationMaster> findByLabId(@Param("labId")UUID labId, Pageable pageable);

	 @Query("SELECT t FROM TestConfigurationMaster t " +
	           "JOIN t.sampleMapping s " +
	           "WHERE (:searchText IS NULL OR LOWER(s.testName) LIKE LOWER(CONCAT('%', :searchText, '%'))) " +
	           "AND t.labId = :labId")
	    Page<TestConfigurationMaster> findByTestName(
	            @Param("searchText") String searchText,
	            @Param("labId") UUID labId,
	            Pageable pageable);

	    @Query("SELECT t FROM TestConfigurationMaster t WHERE t.labId = :labId")
	    Page<TestConfigurationMaster> findByLabId(@Param("labId") UUID labId, Pageable pageable);

	    @Modifying
	    @Query("UPDATE TestConfigurationMaster t SET t.sampleMapping = NULL, t.labDepartment = NULL WHERE t.id = :id")
	    void removeMappingsBeforeDelete(@Param("id") UUID id);

	    
}
