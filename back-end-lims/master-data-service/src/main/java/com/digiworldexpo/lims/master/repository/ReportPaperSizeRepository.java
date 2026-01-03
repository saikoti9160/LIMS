package com.digiworldexpo.lims.master.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.digiworldexpo.lims.entities.master.ReportPaperSize;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReportPaperSizeRepository extends JpaRepository<ReportPaperSize, UUID> {

	Optional<ReportPaperSize> findByPaperSizeAndCreatedBy(String paperSize, UUID createdBy);

	List<ReportPaperSize> findByCreatedBy(UUID createdBy);
}
