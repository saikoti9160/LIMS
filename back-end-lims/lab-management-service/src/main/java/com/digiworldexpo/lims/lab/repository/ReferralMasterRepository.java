package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.ReferralMaster;
@Repository
public interface ReferralMasterRepository extends JpaRepository< ReferralMaster, UUID> {
	
	@Query("SELECT r FROM ReferralMaster r WHERE " +
		       "r.createdBy = :createdBy AND " +
		       "r.active = :active AND " +
		       "(" +
		       "(:keyword IS NULL OR LOWER(r.referralName) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
		       "(:keyword IS NULL OR LOWER(r.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
		       ")")
		List<ReferralMaster> findByCreatedByAndActiveAndReferralName(UUID createdBy, boolean active, String keyword);

		@Query(value = "SELECT LPAD(CAST(nextval('referral_sequence_id') AS TEXT), 4, '0')", nativeQuery = true)
	    String getNextFormattedReferralMasterSequenceId();
	
	
	  List<ReferralMaster> findAllByCreatedByAndActive(UUID createdBy, boolean active);


}

