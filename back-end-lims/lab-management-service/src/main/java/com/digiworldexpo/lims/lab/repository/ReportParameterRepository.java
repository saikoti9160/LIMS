package com.digiworldexpo.lims.lab.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.digiworldexpo.lims.entities.lab_management.ReportParameter;

public interface ReportParameterRepository extends JpaRepository<ReportParameter, UUID> {
	  @Query("SELECT t FROM ReportParameter t "
		         + "WHERE (:searchText IS NULL OR :searchText = '' OR LOWER(t.testParameter) LIKE LOWER(CONCAT('%', :searchText, '%'))) ")
		    Page<ReportParameter> findTestParametersWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	Page<ReportParameter> findAllByLabId(PageRequest pageable, UUID labId);
		}
 