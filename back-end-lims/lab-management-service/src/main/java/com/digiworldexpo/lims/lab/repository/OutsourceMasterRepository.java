package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.OutsourceMaster;
@Repository	
public interface OutsourceMasterRepository extends JpaRepository<OutsourceMaster, UUID> {
	
	 @Query(value = "SELECT LPAD(CAST(nextval('outsource_sequence_id') AS TEXT), 4, '0')", nativeQuery = true)
	    String getNextFormattedOutsourceSequenceId();
	 
	 @Query("SELECT o FROM OutsourceMaster o WHERE o.createdBy = :createdBy AND " +
		       "(" +
		       "(:keyword IS NULL OR LOWER(o.outsourceCenterName) LIKE LOWER(CONCAT('%', :keyword, '%'))) OR " +
		       "(:keyword IS NULL OR LOWER(o.email) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
		       ")")
		List<OutsourceMaster> findByCreatedByAndKeyword(UUID createdBy, String keyword);

	    List<OutsourceMaster> findByCreatedBy(UUID createdBy);

}
