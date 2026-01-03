package com.digiworldexpo.lims.master.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.ReportFontType;

@Repository
public interface ReportFontTypeRepository extends JpaRepository<ReportFontType, UUID> {

	Optional<ReportFontType> findByFontTypeAndCreatedBy(String fontName, UUID createdBy);

	List<ReportFontType> findByCreatedBy(UUID createdBy);
}
