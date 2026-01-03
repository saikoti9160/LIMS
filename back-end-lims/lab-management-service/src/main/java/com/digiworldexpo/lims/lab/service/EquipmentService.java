package com.digiworldexpo.lims.lab.service;

import java.util.UUID;

import com.digiworldexpo.lims.entities.lab_management.Equipment;
import com.digiworldexpo.lims.lab.model.ResponseModel;

public interface EquipmentService {

	ResponseModel<Equipment> deleteEquipmentById(UUID equipmentId);
}
