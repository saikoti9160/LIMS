package com.digiworldexpo.lims.lab.serviceimpl;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.lab_management.WarehouseMaster;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.WarehouseMasterRepository;
import com.digiworldexpo.lims.lab.request.WarehouseMasterRequestDto;
import com.digiworldexpo.lims.lab.response.WarehouseMasterResponseDTO;
import com.digiworldexpo.lims.lab.response.WarehouseSearchResponseDTO;
import com.digiworldexpo.lims.lab.service.WarehouseMasterService;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class WarehouseMasterServiceImpl implements WarehouseMasterService {
	
	private final WarehouseMasterRepository warehouseMasterRepository;
	private final LabRepository labRepository;

	public WarehouseMasterServiceImpl(WarehouseMasterRepository warehouseMasterRepository,
			LabRepository labRepository) {
		super();
		this.warehouseMasterRepository = warehouseMasterRepository;
		this.labRepository = labRepository;
	}

	@Override
	public ResponseModel<WarehouseMasterResponseDTO> saveWarehouseMaster(WarehouseMasterRequestDto warehouseMasterRequestDto, UUID userId) {
	    log.info("Begin WarehouseMasterServiceImpl -> saveWarehouseMaster() method");

	    ResponseModel<WarehouseMasterResponseDTO> response = new ResponseModel<>();

	    try {

	        WarehouseMaster warehouseMaster = convertToEntity(warehouseMasterRequestDto);
	        warehouseMaster.setCreatedBy(userId);
	        
	        if (warehouseMasterRequestDto.getLabId() != null) {
	            Lab lab = labRepository.findById(warehouseMasterRequestDto.getLabId())
	                    .orElseThrow(() -> new IllegalArgumentException("Invalid Lab ID"));
	            warehouseMaster.setLab(lab);
	       	}

	        WarehouseMaster savedWarehouseMaster = warehouseMasterRepository.save(warehouseMaster);

	        WarehouseMasterResponseDTO savedDTO = convertToResponseDTO(savedWarehouseMaster);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Warehouse saved successfully");
	        response.setData(savedDTO);

	        log.info("WarehouseMaster saved with ID: {}", savedWarehouseMaster.getId());

	    } catch (IllegalArgumentException e) {
	        log.error("Validation error while saving warehouse: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	        response.setMessage(e.getMessage());
	        response.setData(null);

	    } catch (Exception e) {
	        log.error("Error occurred while saving warehouse: {}", e.getMessage(), e);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("Failed to save warehouse");
	        response.setData(null);
	    }

	    log.info("End WarehouseMasterServiceImpl -> saveWarehouseMaster() method");
	    return response;
	}

	private WarehouseMaster convertToEntity(WarehouseMasterRequestDto dto) {
		WarehouseMaster entity = new WarehouseMaster();
		BeanUtils.copyProperties(dto, entity);
		return entity;
	}

	private WarehouseMasterResponseDTO convertToResponseDTO(WarehouseMaster entity) {
		WarehouseMasterResponseDTO dto = new WarehouseMasterResponseDTO();
	    BeanUtils.copyProperties(entity, dto);
	    return dto;
	}
	
	@Override
	public ResponseModel<List<WarehouseSearchResponseDTO>> getAllWarehouses(
	        UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize) {
	    
	    log.info("Begin WarehouseMasterServiceImpl -> getAllWarehouses()");

	    ResponseModel<List<WarehouseSearchResponseDTO>> response = new ResponseModel<>();
	    try {
	        List<WarehouseMaster> warehouseList;

	        if (keyword != null && !keyword.trim().isEmpty()) {
	            warehouseList = warehouseMasterRepository.findByCreatedByAndActiveAndKeyword(createdBy, flag, keyword);
	        } else {
	            warehouseList = warehouseMasterRepository.findAllByCreatedByAndActive(createdBy, true);
	        }

	        List<WarehouseSearchResponseDTO> warehouseDTOs = warehouseList.stream()
	                .map(warehouse -> new WarehouseSearchResponseDTO(
	                        warehouse.getId(),
	                        warehouse.getWarehouseName() != null ? warehouse.getWarehouseName() : "N/A"))
	                .collect(Collectors.toList());

	        int totalSize = warehouseDTOs.size();
	        int start = pageNumber * pageSize;
	        int end = Math.min(start + pageSize, totalSize);

	        List<WarehouseSearchResponseDTO> paginatedList =
	                (start < totalSize) ? warehouseDTOs.subList(start, end) : Collections.emptyList();

	        response.setData(paginatedList);
	        response.setTotalCount(totalSize);
	        response.setPageNumber(pageNumber);
	        response.setPageSize(pageSize);
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Warehouses fetched successfully.");

	        log.info("Successfully fetched {} warehouses.", paginatedList.size());
	    } catch (IllegalArgumentException e) {
	        log.error("Invalid pagination parameters: {}", e.getMessage());
	        response.setData(null);
	        response.setMessage("Invalid pagination parameters.");
	        response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	    } catch (Exception e) {
	        log.error("Error fetching warehouses: {}", e.getMessage());
	        response.setData(null);
	        response.setMessage("Failed to fetch warehouses.");
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	    }

	    log.info("End WarehouseMasterServiceImpl -> getAllWarehouses()");
	    return response;
	}

	@Override
	public ResponseModel<WarehouseMasterResponseDTO> getById(UUID id) {
		log.info("Begin WarehouseMasterServiceImpl -> getById() method");

		ResponseModel<WarehouseMasterResponseDTO> response = new ResponseModel<>();

		try {
			WarehouseMaster warehouseMaster = warehouseMasterRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Warehouse not found for ID: " + id));
			WarehouseMasterResponseDTO warehouseDTO = convertToResponseDTO(warehouseMaster);

			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Warehouse fetched successfully.");
			response.setData(warehouseDTO);

			log.info("Successfully fetched warehouse with ID: {}", id);
		} catch (RecordNotFoundException e) {
			log.error("Invalid ID provided: {}", e.getMessage());
			response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			response.setMessage(e.getMessage());
			response.setData(null);
		} catch (Exception e) {
			log.error("Unexpected error occurred while fetching warehouse.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage("Failed to fetch warehouse.");
			response.setData(null);
		}

		log.info("End WarehouseMasterServiceImpl -> getById() method");
		return response;
	}
	
	@Override
	public ResponseModel<WarehouseMasterResponseDTO> updateWarehouseMaster(UUID id,
			WarehouseMasterRequestDto warehouseMasterRequestDto) {
		log.info("Begin WarehouseMasterServiceImpl -> updateWarehouseMaster() method");

		ResponseModel<WarehouseMasterResponseDTO> response = new ResponseModel<>();

		try {
			WarehouseMaster existingWarehouseMaster = warehouseMasterRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Warehouse not found for ID: " + id));

			BeanUtils.copyProperties(warehouseMasterRequestDto, existingWarehouseMaster);

			WarehouseMaster updatedWarehouseMaster = warehouseMasterRepository.save(existingWarehouseMaster);

			WarehouseMasterResponseDTO updatedWarehouseDto = convertToResponseDTO(updatedWarehouseMaster);

			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Warehouse updated successfully.");
			response.setData(updatedWarehouseDto);

			log.info("Successfully updated warehouse with ID: {}", id);
		} catch (RecordNotFoundException e) {
			log.error("Warehouse update failed: {}", e.getMessage());
			response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			response.setMessage(e.getMessage());
			response.setData(null);
		} catch (Exception e) {
			log.error("Unexpected error occurred while updating warehouse.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage("Failed to update warehouse.");
			response.setData(null);
		}

		log.info("End WarehouseMasterServiceImpl -> updateWarehouseMaster() method");
		return response;
	}

	@Override
	public ResponseModel<WarehouseMasterResponseDTO> deleteWarehouseMaster(UUID id) {
		log.info("Begin WarehouseMasterServiceImpl -> deleteWarehouseMaster() method");

		ResponseModel<WarehouseMasterResponseDTO> response = new ResponseModel<>();

		try {
			WarehouseMaster existingWarehouseMaster = warehouseMasterRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Warehouse not found for ID: " + id));

			WarehouseMasterResponseDTO deletedWarehouseDTO = convertToResponseDTO(existingWarehouseMaster);
			warehouseMasterRepository.delete(existingWarehouseMaster);
			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("Warehouse deleted successfully.");
			response.setData(deletedWarehouseDTO);

			log.info("Successfully deleted warehouse with ID: {}", id);
		} catch (RecordNotFoundException e) {
			log.error("Warehouse deletion failed: {}", e.getMessage());
			response.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			response.setMessage(e.getMessage());
			response.setData(null);
		} catch (Exception e) {

			log.error("Unexpected error occurred while deleting warehouse.");
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage("Failed to delete warehouse.");
			response.setData(null);
		}

		log.info("End WarehouseMasterServiceImpl -> deleteWarehouseMaster() method");
		return response;
	}

}

