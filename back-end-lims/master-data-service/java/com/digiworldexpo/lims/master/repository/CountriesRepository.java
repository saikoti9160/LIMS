package com.digiworldexpo.lims.master.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Countries;

@Repository
public interface CountriesRepository extends JpaRepository<Countries, UUID> {

	@Query("SELECT c FROM Countries c "
	        + "WHERE c.active=true AND "
	        + "(LOWER(COALESCE(c.continentCode, '')) LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "LOWER(COALESCE(c.countryName, '')) LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "LOWER(COALESCE(c.countryCode, '')) LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "LOWER(COALESCE(c.phoneCode, '')) LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "LOWER(COALESCE(c.currency, '')) LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "LOWER(COALESCE(c.currencySymbol, '')) LIKE LOWER(CONCAT('%', :startsWith, '%')) OR "
	        + "LOWER(COALESCE(c.continentName, '')) LIKE LOWER(CONCAT('%', :startsWith, '%')))")
	List<Countries> getAllCountriesByParams(@Param("startsWith") String startsWith,  Sort sort);
	
	@Query("SELECT c FROM Countries c "
	        + "WHERE c.active=true AND "
	        + "LOWER(COALESCE(c.countryName, '')) LIKE LOWER(CONCAT('%', :startsWith, '%'))")
	List<Countries> getAllCountriesByParam(@Param("startsWith") String startsWith,  Sort sort);


}
