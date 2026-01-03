package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.Branch;
import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.lab.dto.BranchDto;

@Repository
public interface BranchRepository extends JpaRepository<Branch, UUID> {
	void deleteAllByLab(Lab lab);

	@Query("SELECT new com.digiworldexpo.lims.lab.dto.BranchDto(b.id, b.branchName, b.branchType, b.contactPerson, b.email, b.phoneNumber, b.continent, b.country, b.state, b.city, b.address, b.zipCode, b.lab.id) "
			+ "FROM Branch b WHERE b.lab.id = :id AND b.active= true")
	List<BranchDto> findByLabId(UUID id);

}
