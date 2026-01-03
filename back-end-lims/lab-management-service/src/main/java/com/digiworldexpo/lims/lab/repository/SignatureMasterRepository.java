package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.SignatureMaster;
@Repository
public interface SignatureMasterRepository extends JpaRepository<SignatureMaster, UUID> {
	
	@Query("SELECT s FROM SignatureMaster s WHERE " +
		       "s.createdBy = :createdBy AND " +
		       "s.active = :active AND " +
		       "(:signerName IS NULL OR LOWER(s.signerName) LIKE LOWER(CONCAT('%', :signerName, '%')))")
		List<SignatureMaster> findByCreatedByAndActiveAndSignerName(UUID createdBy, boolean active, String signerName);

		List<SignatureMaster> findAllByCreatedByAndActive(UUID createdBy, boolean active);
		long countByCreatedByAndActive(UUID createdBy, boolean active);





}
