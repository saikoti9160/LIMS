package com.digiworldexpo.lims.master.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.ReportDateFormat;

@Repository
public interface ReportDateFormatRepository extends JpaRepository<ReportDateFormat, UUID> {

	Optional<ReportDateFormat> findByDateFormatAndCreatedBy(String dateFormat, UUID createdBy);

	List<ReportDateFormat> findByCreatedBy(UUID createdBy);
	
}	
