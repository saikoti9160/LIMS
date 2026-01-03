package com.digiworldexpo.lims.lab.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.LabEquipment;


@Repository
public interface LabEquipmentRepository extends JpaRepository<LabEquipment, UUID> {

	@Query("SELECT s FROM Equipment s WHERE "
            + "(:searchText IS NULL OR LOWER(s.equipmentName) LIKE %:searchText%)")
	Page<LabEquipment> findByLabIdAndNameContainingIgnoreCase(UUID labId, String searchText, Pageable pageable);

	Page<LabEquipment> findByLabId(UUID labId, Pageable pageable);

}
