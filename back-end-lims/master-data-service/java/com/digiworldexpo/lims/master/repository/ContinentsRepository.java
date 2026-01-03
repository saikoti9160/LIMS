package com.digiworldexpo.lims.master.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Continents;

@Repository
public interface ContinentsRepository extends JpaRepository<Continents, UUID>{

	@Query("SELECT c FROM Continents c "
	        + "WHERE c.active=true AND (LOWER(COALESCE(c.continentName, '')) LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "LOWER(COALESCE(c.continentCode, '')) LIKE LOWER(CONCAT('%', :startsWith, '%')))")
	List<Continents> getAllContinentsByParams(@Param("startsWith") String startsWith,  Sort sort);
}
