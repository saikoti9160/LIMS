package com.digiworldexpo.lims.master.service.serviceImpl;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.master.BranchType;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.BranchTypeRepository;
import com.digiworldexpo.lims.master.service.BranchTypeService;

@Service
public class BranchTypeServiceImpl implements BranchTypeService {

	@Autowired
	public BranchTypeRepository branchTyeRepository;

	ResponseModel<BranchType> response = new ResponseModel<>();

	@Override
	public ResponseModel<BranchType> addBranchType(BranchType branchType, UUID createdBy) {

		try {
			if (branchTyeRepository.findByBranchTypeName(branchType.getBranchTypeName()).isPresent()) {
				throw new DuplicateRecordFoundException("Branch type is already there");
			}
			branchType.setCreatedBy(createdBy);
			BranchType branch = branchTyeRepository.save(branchType);
			response.setData(branch);
			response.setStatusCode(HttpStatus.CREATED.toString());
			response.setMessage("branch Type fteched sucessfully");

		} catch (DuplicateRecordFoundException d) {
			response.setData(null);
			response.setStatusCode(HttpStatus.CONFLICT.toString());
			response.setMessage(d.getMessage());
		} catch (Exception e) {
			response.setData(null);
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			response.setMessage(e.getMessage());
		}
		return response;
	}

	@Override
	public ResponseModel<BranchType> getBranchType(UUID id) {
		try {
			BranchType branchInfo = branchTyeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("no record found with your given Id"));
			response.setData(branchInfo);
			response.setMessage("record fetched sucessfuly");
			response.setStatusCode(HttpStatus.OK.toString());
		} catch (RecordNotFoundException recordNotFoundException) {
			response.setData(null);
			response.setStatusCode(HttpStatus.NOT_FOUND.toString());
			response.setMessage(recordNotFoundException.getMessage());
		} catch (Exception e) {
			response.setData(null);
			response.setMessage(e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		return response;
	}

	@Override
	public ResponseModel<List<BranchType>> getAllBranches(String searchKeyWord, int pageNumber, int pageSize,
			String sortBy, UUID createdBy) {
		ResponseModel<List<BranchType>> response = new ResponseModel<>();
		try {
			Sort sort = Sort.by(Direction.ASC, sortBy);
			Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

			Page<BranchType> branchType;
			if (searchKeyWord != null && searchKeyWord.isEmpty()) {
				branchType = branchTyeRepository.findByCreatedByAndBranchTypeNameStartingWith(createdBy, searchKeyWord,
						pageable);
			} else {
				branchType = branchTyeRepository.findByCreatedBy(createdBy, pageable);
			}
			response.setData(branchType.getContent());
			response.setMessage("date fetched SucessFully");
			response.setStatusCode(HttpStatus.OK.toString());
			response.setPageNumber(pageNumber);
			response.setSortedBy(sortBy);
			response.setPageSize(pageSize);
			response.setTotalCount((int) branchType.getTotalElements());

		} catch (Exception e) {
			response.setData(null);
			response.setMessage(e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		return response;
	}

	@Override
	public ResponseModel<BranchType> updateBranch(UUID id, UUID modifiedBy, BranchType branchType) {
		try {
			BranchType existingBranchType = branchTyeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("record not found with id"));

			if (branchTyeRepository.findByBranchTypeName(branchType.getBranchTypeName()).isPresent()) {
				throw new DuplicateRecordFoundException("Branch type is already there");
			}
			existingBranchType.setBranchTypeName(branchType.getBranchTypeName());
			existingBranchType.setModifiedBy(modifiedBy);
			existingBranchType.setModifiedOn(new Timestamp(System.currentTimeMillis()));
			BranchType branchResponse = branchTyeRepository.save(existingBranchType);
			response.setData(branchResponse);
			response.setMessage("branch Type updates sucessfully");
			response.setStatusCode(HttpStatus.OK.toString());

		} catch (DuplicateRecordFoundException d) {
			response.setData(null);
			response.setStatusCode(HttpStatus.CONFLICT.toString());
			response.setMessage(d.getMessage());
		} catch (RecordNotFoundException recordNotFoundException) {
			response.setData(null);
			response.setMessage(recordNotFoundException.getMessage());
			response.setStatusCode(HttpStatus.OK.toString());

		} catch (Exception e) {
			response.setData(null);
			response.setMessage(e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		return response;
	}

	@Override
	public ResponseModel<BranchType> deleteBranchType(UUID id) {
		try {
			BranchType existingBranchType = branchTyeRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("record not found with id"));
			existingBranchType.setActive(false);
			BranchType branch = branchTyeRepository.save(existingBranchType);
			response.setData(branch);
			response.setStatusCode(HttpStatus.OK.toString());
			response.setMessage("branch type deleted sucessfully");
		} catch (RecordNotFoundException recordNotFoundException) {
			response.setData(null);
			response.setMessage(recordNotFoundException.getMessage());
			response.setStatusCode(HttpStatus.OK.toString());

		} catch (Exception e) {
			response.setData(null);
			response.setMessage(e.getMessage());
			response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		}
		return response;
	}

}
