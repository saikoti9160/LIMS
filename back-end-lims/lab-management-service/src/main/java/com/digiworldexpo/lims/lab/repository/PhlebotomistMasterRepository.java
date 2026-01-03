package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digiworldexpo.lims.entities.lab_management.PhlebotomistMaster;
import com.digiworldexpo.lims.lab.response.PhlebotomistMasterSearchResponse;

public interface PhlebotomistMasterRepository extends JpaRepository<PhlebotomistMaster, UUID> {

    @Query("SELECT new com.digiworldexpo.lims.lab.response.PhlebotomistMasterSearchResponse(p.id, p.phlebotomistSequenceId, p.email, p.name) " +
           "FROM PhlebotomistMaster p " +
           "WHERE p.createdBy = :createdBy " +
           "AND p.active = :flag " +
           "AND (:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<PhlebotomistMasterSearchResponse> findByCreatedByAndActiveAndKeyword(
            @Param("createdBy") UUID createdBy,
            @Param("flag") Boolean flag,
            @Param("keyword") String keyword);

    List<PhlebotomistMaster> findAllByCreatedByAndActive(UUID createdBy, boolean active);

    @Query(value = "SELECT LPAD(CAST(nextval('phlebotomist_sequence_id') AS TEXT), 4, '0')", nativeQuery = true)
    String getNextFormattedPhlebotomistSequenceId();
}
