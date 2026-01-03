package com.digiworldexpo.lims.lab.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.Lab;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LabRepository extends JpaRepository<Lab, UUID>, JpaSpecificationExecutor<Lab> {

    Optional<Lab> findById(UUID labId);
//    Page<Lab> findAll(Pageable pageable);
    @Query("SELECT l FROM Lab l " +
    	       "WHERE (:keyword IS NULL OR LOWER(CAST(l.labName AS string)) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
    	       "AND (:startDate IS NULL OR l.createdOn >= :startDate) " +
    	       "AND (:endDate IS NULL OR l.createdOn <= :endDate) " +
    	       "AND (:active IS NULL OR l.active = :active) " +
    	       "AND (:country IS NULL OR l.country = :country)")
    	Page<Lab> findAllWithFilters(@Param("keyword") String keyword,
    	                             @Param("startDate") LocalDate startDate,
    	                             @Param("endDate") LocalDate endDate,
    	                             @Param("active") Boolean active,
    	                             @Param("country") String country,
    	                             Pageable pageable);

}
