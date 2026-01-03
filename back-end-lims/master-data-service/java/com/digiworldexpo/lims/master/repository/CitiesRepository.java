package com.digiworldexpo.lims.master.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Cities;

@Repository
public interface CitiesRepository extends JpaRepository<Cities, UUID> {
	@Query(value = "SELECT c FROM Cities c "
	        + "WHERE c.active=true AND (COALESCE(LOWER(c.stateCode), '') LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "COALESCE(LOWER(c.stateName), '') LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "COALESCE(LOWER(c.cityName), '') LIKE LOWER(CONCAT('%', :startsWith, '%'))) ")
	List<Cities> getAllCitiesByParams(@Param("startsWith") String startsWith, Sort sort);
}
