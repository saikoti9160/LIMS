package com.digiworldexpo.lims.master.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.ReportFooterSize;

@Repository
public interface ReportFooterSizeRepository extends JpaRepository<ReportFooterSize, UUID> {

	Optional<ReportFooterSize> findByFooterSizeAndCreatedBy(String pageSize, UUID createdBy);

	List<ReportFooterSize> findByCreatedBy(UUID createdBy);
}
