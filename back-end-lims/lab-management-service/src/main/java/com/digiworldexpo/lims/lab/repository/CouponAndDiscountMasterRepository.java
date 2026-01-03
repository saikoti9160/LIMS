package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.CouponAndDiscountMaster;

@Repository
public interface CouponAndDiscountMasterRepository extends JpaRepository<CouponAndDiscountMaster, UUID> {
	
	
	@Query(value = "SELECT LPAD(CAST(nextval('coupon_sequence_id') AS TEXT), 4, '0')", nativeQuery = true)
	String getNextFormattedSequenceId();

	
    @Query("SELECT cdm FROM CouponAndDiscountMaster cdm WHERE " +
            "LOWER(cdm.couponName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
            "AND cdm.createdBy = :createdBy " +
            "AND cdm.active = :flag")
     List<CouponAndDiscountMaster> findByCouponNameAndCreatedByAndActive(
         @Param("searchTerm") String searchTerm,
         @Param("createdBy") UUID createdBy,
         @Param("flag") Boolean flag
     );
     

    List<CouponAndDiscountMaster> findAllByCreatedByAndActive(UUID createdBy, Boolean active);


}

