package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.WarehouseMaster;
@Repository
public interface WarehouseMasterRepository extends JpaRepository<WarehouseMaster, UUID> {
	
	@Query("SELECT w FROM WarehouseMaster w " +
		       "WHERE w.createdBy = :createdBy AND " +
		       "(COALESCE(:active, true) = true) AND " +  
		       "(:keyword IS NULL OR " +
		       "LOWER(w.warehouseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
		       "LOWER(w.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) ")
		List<WarehouseMaster> findByCreatedByAndActiveAndKeyword(UUID createdBy, boolean active, String keyword);

	List<WarehouseMaster> findAllByCreatedByAndActive(UUID createdBy, boolean active);

}
