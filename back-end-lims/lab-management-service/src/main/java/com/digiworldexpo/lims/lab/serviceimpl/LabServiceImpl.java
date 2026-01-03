package com.digiworldexpo.lims.lab.serviceimpl;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digiworldexpo.lims.entities.lab_management.Branch;
import com.digiworldexpo.lims.entities.lab_management.Equipment;
import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.master.Account;
import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.lab.dto.BranchDto;
import com.digiworldexpo.lims.lab.dto.EquipmentDto;
import com.digiworldexpo.lims.lab.dto.LabDto;
import com.digiworldexpo.lims.lab.dto.LabManagementDTO;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.model.UserModel;
import com.digiworldexpo.lims.lab.repository.AccountRepository;
import com.digiworldexpo.lims.lab.repository.BranchRepository;
import com.digiworldexpo.lims.lab.repository.EquipmentRepository;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.RoleRepository;
import com.digiworldexpo.lims.lab.request.LabFilterRequestDTO;
import com.digiworldexpo.lims.lab.response.LabMainResponse;
import com.digiworldexpo.lims.lab.response.LabResponse;
import com.digiworldexpo.lims.lab.service.LabService;
import com.digiworldexpo.lims.lab.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LabServiceImpl implements LabService {

	@Autowired
	private LabRepository labRepository;

	@Autowired
	private BranchRepository branchRepository;

	@Autowired
	private EquipmentRepository equipmentRepository;
	
	@Autowired
	private RestClientUtil restClientUtil;
	
	@Autowired
	private AccountRepository  accountRepository;
	
	@Autowired
	private RoleRepository roleRepository;
	
	 @PersistenceContext
	    private EntityManager entityManager;

	
	@Value("${lims.auth.signupUrl}")
	private String authSignUpUrl;

	@Override
	public ResponseModel<LabManagementDTO> saveLab(UUID userId, LabManagementDTO labManagementDTO) {
		log.info("Begin of Lab Service Implementation -> saveLab() method");

		ResponseModel<LabManagementDTO> responseModel = new ResponseModel<>();
		try {
			if (labManagementDTO == null || labManagementDTO.getLabDto() == null) {
				throw new IllegalArgumentException("Lab data cannot be null");
			}

			ResponseModel<UUID> signupResponse = signupUser(labManagementDTO);
			if (!HttpStatus.OK.toString().equals(signupResponse.getStatusCode()) || signupResponse.getData() == null) {
			    log.error("User signup failed: {}", signupResponse.getMessage());
			    
				responseModel.setStatusCode(signupResponse.getStatusCode());
				responseModel.setMessage(signupResponse.getMessage());
				responseModel.setData(null);
				return responseModel;
			}

			// Extracting the UUID from signupResponse
			UUID labUserId = signupResponse.getData();
			log.info("Assigned User ID to Lab: {}", labUserId);

			
			Lab lab = new Lab();

			BeanUtils.copyProperties(labManagementDTO.getLabDto(), lab);
			lab.setCreatedOn(new Timestamp(System.currentTimeMillis()));
			lab.setCreatedBy(userId);
			lab.setUserId(labUserId);
			
			lab = labRepository.save(lab);

			if (lab.isHasBranches()) {
				ResponseModel<LabManagementDTO> branchResponse = saveBranches(lab, labManagementDTO.getBranches());
				if (!"200 OK".equals(branchResponse.getStatusCode())) {
					System.out.println("Inside Branch" + branchResponse.getStatusCode());
					return branchResponse;
				}
			}

			ResponseModel<LabManagementDTO> equipmentResponse = saveEquipment(lab, labManagementDTO.getEquipmentList());
			if (!"200 OK".equals(equipmentResponse.getStatusCode())) {
				return equipmentResponse;
			}

			ResponseModel<LabManagementDTO> conversionResponse = convertToLab(lab);
			if (!"200 OK".equals(conversionResponse.getStatusCode())) {
				return conversionResponse; // Return error if conversion fails
			}

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Lab saved successfully");
			responseModel.setData(conversionResponse.getData());

		} catch (IllegalArgumentException illegalArgumentException) {
			log.info("Invalid input data: {}", illegalArgumentException.getMessage());
			throw illegalArgumentException;
		} catch (Exception exception) {
			log.info("Error in createLab(): {}", exception.getMessage());
			throw exception;
		}

		log.info("End of Lab Service Implementation -> createLab() method");
		return responseModel;
	}

	private ResponseModel<UUID> signupUser(LabManagementDTO labManagementDTO) {
		ResponseModel<UUID> responseModel = new ResponseModel<>();
		ResponseEntity<Object> responseEntity = createUserAndReturnResponseEntity(labManagementDTO);

		if (responseEntity.getStatusCodeValue() == 200) {
			try {
				JsonNode jsonNode = new ObjectMapper()
						.readTree(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
				log.info("User response - {}", jsonNode);

				JsonNode userData = jsonNode.get("data");
				if (userData != null && userData.has("id")) {
					UUID userId = UUID.fromString(userData.get("id").asText());
					log.info("User signed up successfully with ID - {}", userId);
					responseModel.setData(userId);
					responseModel.setMessage("User saved successfully");
					responseModel.setStatusCode(HttpStatus.OK.toString());
					return responseModel;
				}
			} catch (JsonProcessingException e) {
				log.error("Error while parsing user response", e);
				responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
				responseModel.setMessage("Failed to process user response data.");
				return responseModel;
			}
		} else if (responseEntity.getStatusCodeValue() == 400) {
			log.error("Failed to save Lab in Auth service.");
			responseModel.setMessage("Failed to save Lab in Auth service.");
			responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
			return responseModel;
		}

		log.error("Error while signing up user in auth service - {}", responseEntity.getBody());
		responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		responseModel.setMessage("User signup failed due to an unexpected error.");
		return responseModel;
	}

	private ResponseEntity<Object> createUserAndReturnResponseEntity(LabManagementDTO labManagementDTO) {
		UserModel userModel = new UserModel();
		Optional<Role> role = roleRepository.findByRoleName("Admin");
		Optional<Account> account = accountRepository.findByAccountName("LAB");

		userModel.setAccountType(account.get().getId());
		userModel.setRole(role.get().getId());
		userModel.setEmail(labManagementDTO.getLabDto().getEmail());
		userModel.setPhone(labManagementDTO.getLabDto().getPhone() != null ? labManagementDTO.getLabDto().getPhone() : "");

		userModel.setPhoneCode(
				labManagementDTO.getLabDto().getPhoneCode() != null ? labManagementDTO.getLabDto().getPhoneCode() : "");
		userModel.setFirstName(
				labManagementDTO.getLabDto().getLabManagerName() != null ? labManagementDTO.getLabDto().getLabManagerName()
						: "");

		userModel.setPassword(labManagementDTO.getLabDto().getPassword() != null ? labManagementDTO.getLabDto().getPassword() : "" );
		return restClientUtil.postForEntity(authSignUpUrl, userModel, Object.class);
	}
	
	
	private ResponseModel<LabManagementDTO> saveBranches(Lab lab, List<BranchDto> branches) {
		log.info("Begin of Lab Service Implementation -> saveBranches() method");

		ResponseModel<LabManagementDTO> responseModel = new ResponseModel<>();
		try {
			for (BranchDto branchDto : branches) {
				Branch branch = new Branch();
				BeanUtils.copyProperties(branchDto, branch);
				branch.setLab(lab); // Associate branch with lab
				branchRepository.save(branch); // Save branch to the database
			}

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Branches saved successfully");

		} catch (Exception exception) {
			log.error("Error in saveBranches(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error occurred while saving branches: " + exception.getMessage());
			responseModel.setData(null); // You can set this to `null` or any relevant data if needed
		}

		log.info("End of Lab Service Implementation -> saveBranches() method");
		return responseModel;
	}

	private ResponseModel<LabManagementDTO> saveEquipment(Lab lab, List<EquipmentDto> equipmentList) {
		log.info("Begin of Lab Service Implementation -> saveEquipment() method");

		ResponseModel<LabManagementDTO> responseModel = new ResponseModel<>();
		try {
			for (EquipmentDto equipmentDto : equipmentList) {
				Equipment equipment = new Equipment();
				BeanUtils.copyProperties(equipmentDto, equipment);
				equipment.setLab(lab); // Associate equipment with lab
				equipmentRepository.save(equipment); // Save equipment to the database
			}

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Equipment saved successfully");

		} catch (Exception exception) {
			log.error("Error in saveEquipment(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error occurred while saving equipment: " + exception.getMessage());
			responseModel.setData(null);
		}

		log.info("End of Lab Service Implementation -> saveEquipment() method");
		return responseModel;
	}

	private ResponseModel<LabManagementDTO> convertToLab(Lab lab) {
		log.info("Begin of Lab Service Implementation -> convertToLab() method");

		ResponseModel<LabManagementDTO> responseModel = new ResponseModel<>();
		LabManagementDTO labManagementDTO = new LabManagementDTO();

		try {
			LabDto labDto = new LabDto();
			BeanUtils.copyProperties(lab, labDto);

			labManagementDTO.setLabDto(labDto);

			if (lab.isHasBranches()) {
				labManagementDTO.setBranches(branchRepository.findByLabId(lab.getId()));
			}

			labManagementDTO.setEquipmentList(equipmentRepository.findByLabId(lab.getId()));

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Lab converted successfully");
			responseModel.setData(labManagementDTO);

		} catch (Exception exception) {
			log.error("Error in convertToLab(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error occurred while converting lab: " + exception.getMessage());
			responseModel.setData(null);
			return responseModel;
		}

		log.info("End of Lab Service Implementation -> convertToLab() method");
		return responseModel;
	}

	@Override
	@Transactional
	public ResponseModel<LabManagementDTO> updateLab(UUID userId, UUID id, LabManagementDTO labManagementDTO) {
		log.info("Begin of Lab Service Implementation -> updateLab() method");

		ResponseModel<LabManagementDTO> responseModel = new ResponseModel<>();
		try {
			Lab lab = labRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("Lab not found with ID: " + id));

			BeanUtils.copyProperties(labManagementDTO.getLabDto(), lab);

			lab.setModifiedOn(new Timestamp(System.currentTimeMillis()));

			lab.setModifiedBy(userId);

			if (lab.isHasBranches()) {
				ResponseModel<LabManagementDTO> branchResponse = updateBranches(userId,lab, labManagementDTO.getBranches());
				if (!"200 OK".equals(branchResponse.getStatusCode())) {
					return branchResponse;
				}
			}

			ResponseModel<LabManagementDTO> equipmentResponse = updateEquipment(userId,lab,labManagementDTO.getEquipmentList());
			if (!"200 OK".equals(equipmentResponse.getStatusCode())) {
				return equipmentResponse;
			}

			lab = labRepository.save(lab);

			ResponseModel<LabManagementDTO> conversionResponse = convertToLab(lab);
			if (!"200 OK".equals(conversionResponse.getStatusCode())) {
				return conversionResponse;
			}

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Lab updated successfully");
			responseModel.setData(conversionResponse.getData());

		} catch (RecordNotFoundException resourceNotFoundException) {
			log.info("No record found for the given Id: {}", resourceNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage(resourceNotFoundException.getMessage());
			responseModel.setData(null);
			return responseModel;
		} catch (Exception exception) {
			log.info("Error in updateLab(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("An unexpected error occurred while updating the lab: " + exception.getMessage());
			responseModel.setData(null);
			return responseModel;
		}

		log.info("End of Lab Service Implementation -> updateLab() method");
		return responseModel;
	}

	@Transactional
	private ResponseModel<LabManagementDTO> updateBranches(UUID userId, Lab lab, List<BranchDto> branches) {
	    log.info("Begin of Lab Service Implementation -> updateBranches() method");

	    ResponseModel<LabManagementDTO> responseModel = new ResponseModel<>();

	    try {
	        for (BranchDto branchDto : branches) {
	            Branch branch;
	            if (branchDto.getId() != null) {
	                Optional<Branch> existingBranchOpt = branchRepository.findById(branchDto.getId());
	                branch = existingBranchOpt.orElse(new Branch());
	            } else {
	                branch = new Branch(); // Creating new branch if no ID is provided
	                branch.setId(UUID.randomUUID()); // Assign a new UUID for new branch
	            }

	            BeanUtils.copyProperties(branchDto, branch);
	            branch.setLab(lab); // Associate branch with lab
	            branch.setModifiedBy(userId);
	            branch.setModifiedOn(new Timestamp(System.currentTimeMillis()));
	            branchRepository.save(branch); // Save the updated or new branch to the database
	        }

	        responseModel.setStatusCode(HttpStatus.OK.toString());
	        responseModel.setMessage("Branches updated successfully");

	    } catch (Exception exception) {
	        log.error("Error in updateBranches(): {}", exception.getMessage(), exception);
	        responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        responseModel.setMessage("Error occurred while updating branches: " + exception.getMessage());
	        responseModel.setData(null);
	    }

	    log.info("End of Lab Service Implementation -> updateBranches() method");
	    return responseModel;
	}


	@Transactional
	private ResponseModel<LabManagementDTO> updateEquipment(UUID userId,Lab lab, List<EquipmentDto> equipmentList) {
		log.info("Begin of Lab Service Implementation -> updateEquipment() method");

		ResponseModel<LabManagementDTO> responseModel = new ResponseModel<>();

		try {
		    for (EquipmentDto equipmentDto : equipmentList) {
		        Equipment equipment;
		        if (equipmentDto.getId() != null) {
		            Optional<Equipment> existingEquipmentOpt = equipmentRepository.findById(equipmentDto.getId());
		            equipment = existingEquipmentOpt.orElse(new Equipment());
		        } else {
		            equipment = new Equipment(); // Creating new equipment if no ID is provided
		            equipment.setId(UUID.randomUUID()); // Assign a new UUID for new equipment
		        }
		 
		        BeanUtils.copyProperties(equipmentDto, equipment);
		        equipment.setLab(lab); // Associate equipment with lab
				equipment.setModifiedBy(userId);
				equipment.setModifiedOn(new Timestamp(System.currentTimeMillis()));		 
		        equipmentRepository.save(equipment); // Save the updated or new equipment to the database
		    }		 
		    responseModel.setStatusCode(HttpStatus.OK.toString());
		    responseModel.setMessage("Equipment updated successfully");
		 
		} catch (Exception exception) {
		    log.error("Error in updateEquipment(): {}", exception.getMessage(), exception);
		    responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		    responseModel.setMessage("Error occurred while updating equipment: " + exception.getMessage());
		    responseModel.setData(null);
		}

		log.info("End of Lab Service Implementation -> updateEquipment() method");
		return responseModel;
	}

	@Override
	public ResponseModel<LabManagementDTO> getLabById(UUID labId) {
		ResponseModel<LabManagementDTO> responseModel = new ResponseModel<>();

		try {

			Lab lab = labRepository.findById(labId)
					.orElseThrow(() -> new RuntimeException("Lab not found with ID: " + labId));

			ResponseModel<LabManagementDTO> conversionResponse = convertToLab(lab);
			if (!"200 OK".equals(conversionResponse.getStatusCode())) {
				return conversionResponse; // Return error if conversion fails
			}

			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Lab retrieved successfully");
			responseModel.setData(conversionResponse.getData());

		} catch (RuntimeException e) {
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("Lab not found: " + e.getMessage());
		} catch (Exception e) {
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("An error occurred while retrieving the lab: " + e.getMessage());
		}

		return responseModel;
	}

	@Override
	public ResponseModel<LabManagementDTO> deleteLabById(UUID labId) {
		ResponseModel<LabManagementDTO> responseModel = new ResponseModel<>();

		try {
			Lab lab = labRepository.findById(labId)
					.orElseThrow(() -> new RecordNotFoundException("Lab not found with ID: " + labId));

			ResponseModel<LabManagementDTO> conversionResponse = convertToLab(lab);
			if (!"200 OK".equals(conversionResponse.getStatusCode())) {
				return conversionResponse; // Return error if conversion fails
			}

			lab.setActive(false);
			lab = labRepository.save(lab);
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Lab deactivated successfully");
			responseModel.setData(conversionResponse.getData());

		} catch (RecordNotFoundException e) {
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("Lab not found: " + e.getMessage());
		} catch (Exception e) {
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("An error occurred while deactivating the lab: " + e.getMessage());
		}

		return responseModel;
	}

	ResponseModel<LabMainResponse> response = new ResponseModel<>();


	@Override
	public ResponseModel<LabMainResponse> getLabs(LabFilterRequestDTO filterRequest, int pageNumber, int pageSize, String sortBy) {
	    log.info("Begin LabService -> getLabs() method with filter: {}", filterRequest);
 
	    ResponseModel<LabMainResponse> response = new ResponseModel<>();
	    try {
	        if (pageNumber < 0) {
	            pageNumber = 0;
	        }
	        int offset = pageNumber * pageSize;

	        // 1️⃣ Get Registered Labs Count
	        StringBuilder registeredCountQueryBuilder = new StringBuilder();
	        registeredCountQueryBuilder.append("SELECT COUNT(DISTINCT l.id) FROM lab.lab l ")
	            .append("JOIN identity.user u ON l.created_by = u.id ")
	            .append("JOIN masterdata.account a ON u.account = a.id ")
	            .append("WHERE a.account_name = 'SELF' ");

	        appendFilters(registeredCountQueryBuilder, filterRequest, "l");

	        // 2️⃣ Get Total Count (With Filters)
	        StringBuilder totalCountQueryBuilder = new StringBuilder();
	        totalCountQueryBuilder.append("SELECT COUNT(DISTINCT l.id) FROM lab.lab l ")
	            .append("LEFT JOIN identity.user u ON l.created_by = u.id ")
	            .append("WHERE 1 = 1 ");
	        
	        appendFilters(totalCountQueryBuilder, filterRequest, "l");

	        Query totalCountQuery = entityManager.createNativeQuery(totalCountQueryBuilder.toString());
	        setQueryParameters(totalCountQuery, filterRequest);
	        Integer totalCount = ((Number) totalCountQuery.getSingleResult()).intValue();

	        // 3️⃣ Get Active Labs Count (With Filters)
	        StringBuilder activeCountQueryBuilder = new StringBuilder();
	        activeCountQueryBuilder.append("SELECT COUNT(DISTINCT l.id) FROM lab.lab l ")
	            .append("LEFT JOIN identity.user u ON l.created_by = u.id ")
	            .append("WHERE l.active = true ");
	        
	        appendFilters(activeCountQueryBuilder, filterRequest, "l");

	        Query activeCountQuery = entityManager.createNativeQuery(activeCountQueryBuilder.toString());
	        setQueryParameters(activeCountQuery, filterRequest);
	        Integer activeLabsCount = ((Number) activeCountQuery.getSingleResult()).intValue();

	        // Calculate Inactive Labs Count
	        int inactiveLabsCount = totalCount - activeLabsCount;

	        Query registeredCountQuery = entityManager.createNativeQuery(registeredCountQueryBuilder.toString());
	        setQueryParameters(registeredCountQuery, filterRequest);
	        Long registeredLabsCount = (long) ((Number) registeredCountQuery.getSingleResult()).intValue();

	        // 4️⃣ Main Query to Fetch Labs
	        StringBuilder queryBuilder = new StringBuilder();
	        queryBuilder.append("SELECT DISTINCT l.id, l.lab_name, l.email, l.created_on, l.country, l.active, l.logo, ")
	            .append("CONCAT(u.first_name, ' ', u.last_name) AS createdBy ")
	            .append("FROM lab.lab l ")
	            .append("LEFT JOIN identity.user u ON l.created_by = u.id ")
	            .append("WHERE 1 = 1 ");

	        appendFilters(queryBuilder, filterRequest, "l");

	        queryBuilder.append("ORDER BY l.created_on ")
	            .append(sortBy.equalsIgnoreCase("desc") ? "DESC" : "ASC")
	            .append(" LIMIT :pageSize OFFSET :offset");

	        Query query = entityManager.createNativeQuery(queryBuilder.toString());
	        setQueryParameters(query, filterRequest);
	        query.setParameter("pageSize", pageSize);
	        query.setParameter("offset", offset);

	        List<Object[]> labData = query.getResultList();
	        List<LabResponse> labResponses = new ArrayList<>();

	        for (Object[] row : labData) {
	            LabResponse responseItem = new LabResponse();
	            try {
	                responseItem.setId((UUID) row[0]);
	                responseItem.setLabName((String) row[1]);
	                responseItem.setEmail((String) row[2]);
	                responseItem.setCreatedOn((Timestamp) row[3]);
	                responseItem.setCountry((String) row[4]);
	                responseItem.setActive((Boolean) row[5]);
	                responseItem.setLogo((String) row[6]);
	                responseItem.setCreatedBy((String) row[7]);
	            } catch (ClassCastException e) {
	                log.error("Error casting row data: {}", Arrays.toString(row), e);
	            }
	            labResponses.add(responseItem);
	        }

	        LabMainResponse labMainResponse = new LabMainResponse();
	        labMainResponse.setLab(labResponses);
	        labMainResponse.setTotalLabs(totalCount);
	        labMainResponse.setActiveLabs(activeLabsCount);
	        labMainResponse.setTotalinactivelabs(inactiveLabsCount);
	        labMainResponse.setRegisteredLabs(registeredLabsCount);

	        response.setData(labMainResponse);
	        response.setTotalCount(totalCount);
	        response.setPageNumber(pageNumber);
	        response.setPageSize(pageSize);
	        response.setSortedBy(sortBy);
	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Labs retrieved successfully");

	    } catch (Exception exception) {
	        log.error("Error in getLabs(): {}", exception.getMessage(), exception);
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("An error occurred while retrieving labs");
	    }

	    log.info("End LabService -> getLabs() method");
	    return response;
	}
	
	 private void setQueryParameters(Query query, LabFilterRequestDTO filterRequest) {
	        if (filterRequest.getSearchKey() != null) {
	            query.setParameter("searchKey", filterRequest.getSearchKey());
	        }
	        if (filterRequest.getStatus() != null) {
	            query.setParameter("status", filterRequest.getStatus());
	        }
	        if (filterRequest.getStartDate() != null) {
	            query.setParameter("startDate", filterRequest.getStartDate());
	        }
	        if (filterRequest.getEndDate() != null) {
	            query.setParameter("endDate", filterRequest.getEndDate());
	        }
	        if(filterRequest.getContinent()!=null) {
	        	query.setParameter("continent", filterRequest.getContinent());
	        }
	        if (filterRequest.getCountry() != null) {
	            query.setParameter("country", filterRequest.getCountry());
	        }
	        if (filterRequest.getState() != null) {
	            query.setParameter("state", filterRequest.getState());
	        }
	        if (filterRequest.getCity() != null) {
	            query.setParameter("city", filterRequest.getCity());
	        }
	    }

	private void appendFilters(StringBuilder queryBuilder, LabFilterRequestDTO filterRequest, String alias) {
	    if (filterRequest.getSearchKey() != null && !filterRequest.getSearchKey().isEmpty()) {
	        queryBuilder.append("AND (LOWER(").append(alias).append(".lab_name) LIKE LOWER(CONCAT('%', :searchKey, '%')) ")
	            .append("OR LOWER(").append(alias).append(".email) LIKE LOWER(CONCAT('%', :searchKey, '%')) ")
	            .append("OR LOWER(u.first_name) LIKE LOWER(CONCAT('%', :searchKey, '%')) ")
	            .append("OR LOWER(u.last_name) LIKE LOWER(CONCAT('%', :searchKey, '%'))) ");
	    }
	    if (filterRequest.getStatus() != null) {
	        queryBuilder.append("AND ").append(alias).append(".active = :status ");
	    }
	    if (filterRequest.getStartDate() != null) {
	        queryBuilder.append("AND ").append(alias).append(".created_on >= :startDate ");
	    }
	    if (filterRequest.getEndDate() != null) {
	        queryBuilder.append("AND ").append(alias).append(".created_on <= :endDate ");
	    }
	    if (filterRequest.getCountry() != null) {
	        queryBuilder.append("AND ").append(alias).append(".country = :country ");
	    }
	    if (filterRequest.getContinent() != null) {
	        queryBuilder.append("AND ").append(alias).append(".continent = :continent ");
	    }
	    if (filterRequest.getState() != null) {
	        queryBuilder.append("AND ").append(alias).append(".state = :state ");
	    }
	    if (filterRequest.getCity() != null) {
	        queryBuilder.append("AND ").append(alias).append(".city = :city ");
	    }
 

	}



}
	

