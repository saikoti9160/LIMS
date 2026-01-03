package com.digiworldexpo.lims.master.repository;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.SupportPriority;

@Repository
public interface SupportPriorityRepository extends JpaRepository<SupportPriority, UUID> {

	@Query("SELECT sp FROM SupportPriority sp WHERE LOWER(sp.name) LIKE LOWER(CONCAT('%', :startsWith, '%'))")
	List<SupportPriority> findSupportPrioritiesByName(@Param("startsWith") String startsWith, Sort sort);

	Optional<SupportPriority> findByName(String name);

}
