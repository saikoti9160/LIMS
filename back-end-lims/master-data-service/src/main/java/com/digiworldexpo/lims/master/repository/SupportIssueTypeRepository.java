package com.digiworldexpo.lims.master.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.SupportIssueType;

@Repository
public interface SupportIssueTypeRepository extends JpaRepository<SupportIssueType, UUID> {

//	 @Query("SELECT s FROM SupportIssueType s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :name, '%'))")
//	    List<SupportIssueType> findSupportIssueTypesByName(@Param("name") String name, Sort sort);
	Optional<SupportIssueType> findByName(String name);

	@Query("SELECT s FROM SupportIssueType s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :startsWith, '%'))")
	List<SupportIssueType> findSupportIssueTypesByStartsWith(@Param("startsWith") String startsWith, Sort sort);

}
