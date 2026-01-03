package com.digiworldexpo.lims.lab.serviceimpl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digiworldexpo.lims.entities.lab_management.Equipment;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.EquipmentRepository;
import com.digiworldexpo.lims.lab.service.EquipmentService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EquipmentServiceImpl implements EquipmentService {

	@Autowired
	private EquipmentRepository equipmentRepository;


	@Transactional
	public ResponseModel<Equipment> deleteEquipmentById(UUID equipmentId) {
		log.info("Begin EquipmentService -> deleteEquipmentById() method...");
		ResponseModel<Equipment> responseModel = new ResponseModel<>();

		try {
			Optional<Equipment> equipmentOptional = equipmentRepository.findById(equipmentId);
			if (equipmentOptional.isEmpty()) {
				responseModel.setData(null);
				responseModel.setMessage("Equipment not found");
				responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
				return responseModel;
			}

			Equipment equipment = equipmentOptional.get();
			equipment.setActive(false);
			equipmentRepository.save(equipment);

			responseModel.setData(equipment);
			responseModel.setMessage("Equipment soft-deleted successfully");
			responseModel.setStatusCode(HttpStatus.OK.toString());

		} catch (Exception e) {
			log.error("Error occurred while soft-deleting Equipment: {}", e.getMessage());
			responseModel.setData(null);
			responseModel.setMessage("Failed to soft delete Equipment");
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}

		log.info("End EquipmentService -> deleteEquipmentById() method...");
		return responseModel;
	}
}
