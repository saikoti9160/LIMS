package com.digiworldexpo.lims.lab.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.Equipment;
import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.lab.dto.EquipmentDto;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
	void deleteAllByLab(Lab lab);

	@Query("SELECT new com.digiworldexpo.lims.lab.dto.EquipmentDto(e.id, e.equipmentName, e.model, e.lab.id) "
			+ "FROM Equipment e WHERE e.lab.id = :labId AND e.active = true")
	List<EquipmentDto> findByLabId(UUID labId);
}
