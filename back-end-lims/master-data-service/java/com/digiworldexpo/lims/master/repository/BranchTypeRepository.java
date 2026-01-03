package com.digiworldexpo.lims.master.repository;

import java.util.Optional;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.BranchType;

@Repository
public interface BranchTypeRepository extends JpaRepository<BranchType, UUID> {

	Optional<BranchType> findByBranchTypeName(String branchTypeName);

	@Query("SELECT b FROM BranchType b WHERE b.createdBy = :createdBy AND LOWER(b.branchTypeName) LIKE LOWER(CONCAT(:startsWith, '%'))")
	Page<BranchType> findByCreatedByAndBranchTypeNameStartingWith(@Param("createdBy") UUID createdBy,
			@Param("startsWith") String startsWith, Pageable pageable);

	Page<BranchType> findByCreatedBy(UUID createdBy, Pageable pageable);

}
