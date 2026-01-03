package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.DiscountMaster;

@Repository
public interface DiscountMasterRepository extends JpaRepository<DiscountMaster, UUID> {
	
	 @Query("SELECT d FROM DiscountMaster d WHERE d.createdBy = :createdBy " +
	            "AND (LOWER(d.discountName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
	            "OR LOWER(d.discountType) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
	            "AND d.active = :flag")
	    List<DiscountMaster> findByCreatedByAndActiveAndDiscountName(
	        @Param("createdBy") UUID createdBy,
	        @Param("flag") Boolean flag,
	        @Param("keyword") String keyword
	    );

	 List<DiscountMaster> findAllByCreatedByAndActive(UUID createdBy, boolean active);
	    


}