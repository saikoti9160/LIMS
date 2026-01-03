package com.digiworldexpo.lims.master.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digiworldexpo.lims.entities.master.States;

public interface StatesRepository extends JpaRepository<States, UUID> {

	@Query(value = "SELECT s FROM States s "
	        + "WHERE s.active=true AND "
	        + "(COALESCE(LOWER(s.stateName), '') LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "COALESCE(LOWER(s.stateCode), '') LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "COALESCE(LOWER(s.countryName), '') LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "COALESCE(LOWER(s.countryCode), '') LIKE LOWER(CONCAT('%', :startsWith, '%'))) ")
	List<States> getAllStatesByParam(@Param("startsWith") String startsWith, Sort sort);
}
