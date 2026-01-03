package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.DoctorMaster;

@Repository
public interface DoctorMasterRepository extends JpaRepository<DoctorMaster, UUID> {

	@Query("SELECT d FROM DoctorMaster d " +
		       "WHERE d.createdBy = :createdBy " +
		       "AND d.active = :flag " +
		       "AND (:keyword IS NULL OR :keyword = '' OR " +
		       "LOWER(d.doctorName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
		       "OR LOWER(d.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
		List<DoctorMaster> findByCreatedByAndActiveByDoctor(
		        @Param("createdBy") UUID createdBy,
		        @Param("flag") Boolean flag,
		        @Param("keyword") String keyword);


    
    @Query(value = "SELECT LPAD(CAST(nextval('doctor_sequence_id') AS TEXT), 4, '0')", nativeQuery = true)
    String getNextFormattedDoctorSequenceId();
    
    List<DoctorMaster> findAllByCreatedByAndActive(UUID createdBy, boolean active);
}
