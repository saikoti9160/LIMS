package com.digiworldexpo.lims.lab.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.BranchMaster;
import com.digiworldexpo.lims.entities.master.BranchType;

@Repository
public interface BranchMasterRepository extends JpaRepository<BranchMaster,UUID>{

	
	// Check if a branch with the same email exists
    Optional<BranchMaster> findByEmail(String email);
    
    Optional<BranchMaster> findByBranchNameAndBranchType(String branchName, BranchType branchType);

    
    @Query(value = "SELECT 'BR-' || LPAD(CAST(nextval('branch_sequence_id') AS TEXT), 3, '0')", nativeQuery = true)
    String getNextFormattedBranchSequenceId();
    
    @Query("SELECT b FROM BranchMaster b WHERE b.createdBy = :createdBy AND " +
    	       "(LOWER(b.branchSequenceId) LIKE LOWER(CONCAT('%', :searchBy, '%')) OR " +
    	       "LOWER(b.branchName) LIKE LOWER(CONCAT('%', :searchBy, '%')) OR " +
    	       "LOWER(b.branchType.branchTypeName) LIKE LOWER(CONCAT('%', :searchBy, '%')) OR " +
    	       "CAST(b.phoneNumber AS string) LIKE CONCAT('%', :searchBy, '%'))")
    	Page<BranchMaster> searchBranches(@Param("createdBy") UUID createdBy, 
    	                                  @Param("searchBy") String searchBy, 
    	                                  Pageable pageable);



    	long countByCreatedBy(UUID createdBy);

		Page<BranchMaster> findByCreatedBy(UUID createdBy, Pageable pageable);


}
