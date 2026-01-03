package com.digiworldexpo.lims.lab.serviceimpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.digiworldexpo.lims.entities.lab_management.BillAccessConfiguration;
import com.digiworldexpo.lims.entities.lab_management.Lab;
import com.digiworldexpo.lims.entities.lab_management.Organization;
import com.digiworldexpo.lims.entities.lab_management.PatientConfiguration;
import com.digiworldexpo.lims.entities.lab_management.PaymentDetails;
import com.digiworldexpo.lims.entities.lab_management.ReportAccessConfiguration;
import com.digiworldexpo.lims.entities.master.Account;
import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.lab.dto.BillAccessConfigurationDTO;
import com.digiworldexpo.lims.lab.dto.PatientConfigurationDTO;
import com.digiworldexpo.lims.lab.dto.PaymentDetailsDTO;
import com.digiworldexpo.lims.lab.dto.PostPaid;
import com.digiworldexpo.lims.lab.dto.Prepaid;
import com.digiworldexpo.lims.lab.dto.ReportAccessConfigurationDTO;
import com.digiworldexpo.lims.lab.exception.RecordNotFoundException;
import com.digiworldexpo.lims.lab.model.ResponseModel;
import com.digiworldexpo.lims.lab.model.UserModel;
import com.digiworldexpo.lims.lab.repository.AccountRepository;
import com.digiworldexpo.lims.lab.repository.LabRepository;
import com.digiworldexpo.lims.lab.repository.OrganizationRepository;
import com.digiworldexpo.lims.lab.repository.RoleRepository;
import com.digiworldexpo.lims.lab.request.OrganizationRequestDTO;
import com.digiworldexpo.lims.lab.response.OrganizationResponseDTO;
import com.digiworldexpo.lims.lab.response.OrganizationSearchResponseDTO;
import com.digiworldexpo.lims.lab.service.OrganizationService;
import com.digiworldexpo.lims.lab.util.RestClientUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OrganizationServiceImpl implements OrganizationService {
    
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;
    private final RestClientUtil restClientUtil;
    private final LabRepository labRepository;
    private final AccountRepository accountRepository;
    
    @Value("${lims.auth.signupUrl}")
    private String authSignUpUrl;



	public OrganizationServiceImpl(OrganizationRepository organizationRepository, RoleRepository roleRepository,
			RestClientUtil restClientUtil, LabRepository labRepository, AccountRepository accountRepository
			) {
		super();
		this.organizationRepository = organizationRepository;
		this.roleRepository = roleRepository;
		this.restClientUtil = restClientUtil;
		this.labRepository = labRepository;
		this.accountRepository = accountRepository;
	}

	@Override
	public ResponseModel<OrganizationResponseDTO> saveOrganization(UUID createdBy, OrganizationRequestDTO organizationDTO) {
	    log.info("Begin OrganizationService -> save() method");
	    ResponseModel<OrganizationResponseDTO> response = new ResponseModel<>();

	    try {
	        String sequenceId = "Org-" + organizationRepository.getNextFormattedOrganizationSequenceId();
	        log.info("Generated Organization Sequence ID: {}", sequenceId);

	        Organization organizationEntity = mapToEntity(organizationDTO, createdBy);
	        organizationEntity.setCreatedBy(createdBy);
	        organizationEntity.setOrganizationSequenceId(sequenceId);

	        // Handle user signup logic
	        ResponseModel<UUID> signupResponse = signupUser(organizationDTO);
	        if (!HttpStatus.OK.toString().equals(signupResponse.getStatusCode()) || signupResponse.getData() == null) {
	            log.error("User signup failed: {}", signupResponse.getMessage());
	            response.setStatusCode(signupResponse.getStatusCode());
	            response.setMessage(signupResponse.getMessage());
	            response.setData(null);
	            return response;
	        }

	        UUID labUserId = signupResponse.getData();
	        log.info("Assigned User ID to Lab: {}", labUserId);

	        validatePaymentOptions(organizationDTO);

	        // Initialize payment details if not null
	        if (organizationDTO.getPaymentDetails() != null) {
	            PaymentDetails paymentDetails = new PaymentDetails();

	            // Handling prepaid payment details
	            if (organizationDTO.getPaymentDetails().getPrepaid() != null) {
	                paymentDetails.setPrepaidValues(organizationDTO.getPaymentDetails().getPrepaid().stream()
	                        .map(Prepaid::getPrepaidAdvance)
	                        .collect(Collectors.toList()));  // Collect all prepaid advances
	                paymentDetails.setPrepaidPaymentModes(organizationDTO.getPaymentDetails().getPrepaid().stream()
	                        .map(Prepaid::getPaymentMode)
	                        .collect(Collectors.toList()));  // Collect all prepaid payment modes
	            }

	            // Handling postpaid payment details
	            if (organizationDTO.getPaymentDetails().getPostPaid() != null) {
	                paymentDetails.setPostpaidValues(organizationDTO.getPaymentDetails().getPostPaid().stream()
	                        .map(PostPaid::getPostPaidCreditLimit)
	                        .collect(Collectors.toList()));  // Collect all postpaid credit limits
	                paymentDetails.setPostpaidPaymentModes(organizationDTO.getPaymentDetails().getPostPaid().stream()
	                        .map(PostPaid::getPaymentMode)
	                        .collect(Collectors.toList()));  // Collect all postpaid payment modes
	            }

	            paymentDetails.setOrganization(organizationEntity);
	            organizationEntity.setPaymentDetails(paymentDetails);
	        } else {
	            organizationEntity.setPaymentDetails(new PaymentDetails());
	        }

	        // Ensure lab and role are set if provided
	        if (organizationDTO.getLabId() != null) {
	            Optional<Lab> labOptional = labRepository.findById(organizationDTO.getLabId());
	            if (labOptional.isPresent()) {
	                organizationEntity.setLab(labOptional.get());
	            } else {
	                throw new IllegalArgumentException("Lab with ID " + organizationDTO.getLabId() + " not found.");
	            }
	        }

	        if (organizationDTO.getRoleId() != null) {
	            Optional<Role> roleOptional = roleRepository.findById(organizationDTO.getRoleId());
	            if (roleOptional.isPresent()) {
	                organizationEntity.setRole(roleOptional.get());
	            } else {
	                throw new IllegalArgumentException("Role with ID " + organizationDTO.getRoleId() + " not found.");
	            }
	        }

	        // Initialize configurations if not null
	        PatientConfiguration patientConfig = (organizationDTO.getPatientConfiguration() != null) ? 
	            new PatientConfiguration() : new PatientConfiguration(); // Initialize even if null to prevent nulls
	        if (organizationDTO.getPatientConfiguration() != null) {
	            BeanUtils.copyProperties(organizationDTO.getPatientConfiguration(), patientConfig);
	        }
	        patientConfig.setOrganization(organizationEntity);
	        organizationEntity.setPatientConfiguration(patientConfig);

	        ReportAccessConfiguration reportConfig = (organizationDTO.getReportAccessConfiguration() != null) ?
	            new ReportAccessConfiguration() : new ReportAccessConfiguration(); // Initialize if null
	        if (organizationDTO.getReportAccessConfiguration() != null) {
	            BeanUtils.copyProperties(organizationDTO.getReportAccessConfiguration(), reportConfig);
	        }
	        reportConfig.setOrganization(organizationEntity);
	        organizationEntity.setReportAccessConfiguration(reportConfig);

	        BillAccessConfiguration billConfig = (organizationDTO.getBillAccessConfiguration() != null) ?
	            new BillAccessConfiguration() : new BillAccessConfiguration(); // Initialize if null
	        if (organizationDTO.getBillAccessConfiguration() != null) {
	            BeanUtils.copyProperties(organizationDTO.getBillAccessConfiguration(), billConfig);
	        }
	        billConfig.setOrganization(organizationEntity);
	        organizationEntity.setBillAccessConfiguration(billConfig);

	        Organization savedEntity = organizationRepository.save(organizationEntity);
	        OrganizationResponseDTO savedDTO = mapToResponseDTO(savedEntity);

	        response.setStatusCode(HttpStatus.OK.toString());
	        response.setMessage("Organization saved successfully.");
	        response.setData(savedDTO);

	        log.info("Organization saved successfully with ID: {}", savedDTO.getId());
	    } catch (Exception e) {
	        log.error("Error occurred while saving Organization: {}", e.getMessage());
	        response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	        response.setMessage("An error occurred while saving the Organization: " + e.getMessage());
	    }

	    return response;
	}

	private void validatePaymentOptions(OrganizationRequestDTO organizationDTO) {
	    PaymentDetailsDTO paymentDetails = organizationDTO.getPaymentDetails();
	    if (paymentDetails != null) {
	        // Validate prepaid advances
	        if (paymentDetails.getPrepaid() != null && !paymentDetails.getPrepaid().isEmpty()) {
	            paymentDetails.getPrepaid().forEach(prepaid -> {
	                if (prepaid.getPrepaidAdvance() <= 0) {
	                    throw new IllegalArgumentException("Prepaid Advance must be greater than zero.");
	                }
	            });
	        }

	        // Validate postpaid credit limits
	        if (paymentDetails.getPostPaid() != null && !paymentDetails.getPostPaid().isEmpty()) {
	            paymentDetails.getPostPaid().forEach(postPaid -> {
	                if (postPaid.getPostPaidCreditLimit() <= 0) {
	                    throw new IllegalArgumentException("Post-Paid Credit Limit must be greater than zero.");
	                }
	            });
	        }
	    }
	}





	private ResponseModel<UUID> signupUser(OrganizationRequestDTO organizationDTO) {
	    ResponseModel<UUID> responseModel = new ResponseModel<>();
	    ResponseEntity<Object> responseEntity = createUserAndReturnResponseEntity(organizationDTO);

	    if (responseEntity.getStatusCodeValue() == 200) {
	        try {
	            JsonNode jsonNode = new ObjectMapper().readTree(new ObjectMapper().writeValueAsString(responseEntity.getBody()));
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
	        log.error("Failed to save Organization in Auth service.");
	        responseModel.setMessage("Failed to save Organization in Auth service.");
	        responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	        return responseModel;
	    }

	    log.error("Error while signing up user in auth service - {}", responseEntity.getBody());
	    responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	    responseModel.setMessage("User signup failed due to an unexpected error.");
	    return responseModel;
	}

	private ResponseEntity<Object> createUserAndReturnResponseEntity(OrganizationRequestDTO organizationDTO) {
	    UserModel userModel = new UserModel();
	    Optional<Account> account = accountRepository.findByAccountName("LabAdmin");

	    if (!account.isPresent()) {
	        throw new IllegalStateException("Required account 'LabAdmin' not found.");
	    }

	    UUID roleId = organizationDTO.getRoleId();
	    Optional<Role> role = roleRepository.findById(roleId);

	    if (!role.isPresent()) {
	        throw new IllegalStateException("Required role not found.");
	    }

	    userModel.setAccountType(account.get().getId());
	    userModel.setRole(role.get().getId());
	    userModel.setEmail(organizationDTO.getEmail());
	    userModel.setPassword(organizationDTO.getPassword());
	    userModel.setLab(organizationDTO.getLabId());

	    return restClientUtil.postForEntity(authSignUpUrl, userModel, Object.class);
	}

	private Organization mapToEntity(OrganizationRequestDTO dto, UUID createdBy) {
	    Organization entity = new Organization();
	    BeanUtils.copyProperties(dto, entity);
	    return entity;
	}

	private OrganizationResponseDTO mapToResponseDTO(Organization entity) {
	    OrganizationResponseDTO dto = new OrganizationResponseDTO();
	    BeanUtils.copyProperties(entity, dto);
	    
	    // Map PaymentDetails
	    if (entity.getPaymentDetails() != null) {
	        PaymentDetailsDTO paymentDetailsDTO = new PaymentDetailsDTO();
	        
	        // Mapping Prepaid values
	        if (entity.getPaymentDetails().getPrepaidValues() != null && entity.getPaymentDetails().getPrepaidPaymentModes() != null) {
	            List<Prepaid> prepaidList = new ArrayList<>();
	            for (int i = 0; i < entity.getPaymentDetails().getPrepaidValues().size(); i++) {
	                prepaidList.add(new Prepaid(entity.getPaymentDetails().getPrepaidValues().get(i),
	                                             entity.getPaymentDetails().getPrepaidPaymentModes().get(i)));
	            }
	            paymentDetailsDTO.setPrepaid(prepaidList);
	        }
	        
	        // Mapping PostPaid values
	        if (entity.getPaymentDetails().getPostpaidValues() != null && entity.getPaymentDetails().getPostpaidPaymentModes() != null) {
	            List<PostPaid> postPaidList = new ArrayList<>();
	            for (int i = 0; i < entity.getPaymentDetails().getPostpaidValues().size(); i++) {
	                postPaidList.add(new PostPaid(entity.getPaymentDetails().getPostpaidValues().get(i),
	                                              entity.getPaymentDetails().getPostpaidPaymentModes().get(i)));
	            }
	            paymentDetailsDTO.setPostPaid(postPaidList);
	        }
	        
	        // Setting the PaymentDetailsDTO in the DTO object
	        dto.setPaymentDetails(paymentDetailsDTO);
	    }


	    // Map PatientConfiguration
	    if (entity.getPatientConfiguration() != null) {
	        PatientConfigurationDTO patientConfigDTO = new PatientConfigurationDTO();
	        BeanUtils.copyProperties(entity.getPatientConfiguration(), patientConfigDTO);
	        dto.setPatientConfiguration(patientConfigDTO);
	    }

	    // Map ReportAccessConfiguration
	    if (entity.getReportAccessConfiguration() != null) {
	        ReportAccessConfigurationDTO reportConfigDTO = new ReportAccessConfigurationDTO();
	        BeanUtils.copyProperties(entity.getReportAccessConfiguration(), reportConfigDTO);
	        dto.setReportAccessConfiguration(reportConfigDTO);
	    }

	    // Map BillAccessConfiguration
	    if (entity.getBillAccessConfiguration() != null) {
	        BillAccessConfigurationDTO billConfigDTO = new BillAccessConfigurationDTO();
	        BeanUtils.copyProperties(entity.getBillAccessConfiguration(), billConfigDTO);
	        dto.setBillAccessConfiguration(billConfigDTO);
	    }

	    // Ensure Lab and Role are also mapped
	    if (entity.getLab() != null) {
	        dto.setLabId(entity.getLab().getId());  // Ensure this is set correctly
	    }

	    if (entity.getRole() != null) {
	        dto.setRoleId(entity.getRole().getId());  // Ensure this is set correctly
	    }

	    return dto;
	}

    
    @Override
    public ResponseModel<List<OrganizationSearchResponseDTO>> getAllOrganization(
            UUID createdBy, String keyword, Boolean flag, Integer pageNumber, Integer pageSize) {

        log.info("Begin OrganizationServiceImpl -> getAllOrganization() method");

        ResponseModel<List<OrganizationSearchResponseDTO>> response = new ResponseModel<>();

        try {
            List<OrganizationSearchResponseDTO> organizationDTOs = new ArrayList<>();
            List<Organization> organizations;

            if (keyword != null && !keyword.trim().isEmpty()) {
                organizations = organizationRepository.findByCreatedByAndActiveAndKeyword(createdBy, flag, keyword);
            } else {
                organizations = organizationRepository.findAllByCreatedByAndActive(createdBy, true);
            }

            organizationDTOs = organizations.stream()
                    .map(org -> new OrganizationSearchResponseDTO(
                            org.getId(),
                            org.getOrganizationSequenceId(),
                            org.getName(),
                            org.getPhoneNumber(),
                            org.getEmail()))
                    .collect(Collectors.toList());

            int start = pageNumber * pageSize;
            int end = Math.min(start + pageSize, organizationDTOs.size());
            List<OrganizationSearchResponseDTO> subList = organizationDTOs.subList(start, end);

            response.setData(subList);
            response.setTotalCount(organizationDTOs.size());
            response.setPageNumber(pageNumber);
            response.setPageSize(pageSize);
            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Organizations fetched successfully.");

        } catch (Exception e) {
            log.error("Error in OrganizationServiceImpl -> getAllOrganization() method: {}", e.getMessage());
            response.setData(null);
            response.setMessage("Failed to fetch organizations.");
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        }

        log.info("End OrganizationServiceImpl -> getAllOrganization() method");
        return response;
    }
    
    @Override
    public ResponseModel<OrganizationResponseDTO> getOrganizationById(UUID id) {
        log.info("Begin OrganizationServiceImpl -> getById() method");

        ResponseModel<OrganizationResponseDTO> response = new ResponseModel<>();
        
        try {
            Organization organization = organizationRepository.findById(id)
                    .orElseThrow(() -> new RecordNotFoundException("Organization not found with ID: " + id));

            OrganizationResponseDTO organizationDTO = mapToResponseDTO(organization);

            response.setData(organizationDTO);
            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Organization retrieved successfully.");
            log.info("Organization with ID: {} found and converted to DTO", id);

        } catch (RecordNotFoundException e) {
            log.error("Organization not found: {}", e.getMessage());
            response.setStatusCode(HttpStatus.NOT_FOUND.toString());
            response.setMessage(e.getMessage());
            response.setData(null);
        } catch (Exception e) {
            log.error("Error occurred while retrieving organization with ID {}: {}", id, e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage("An error occurred while retrieving the Organization.");
            response.setData(null);
        }
        
        log.info("End OrganizationServiceImpl -> getById() method");
        return response;
    }
    
    
//    @Override
//    public ResponseModel<OrganizationResponseDTO> updateOrganizationById(UUID id, OrganizationRequestDTO organizationRequestDTO) {
//        log.info("Begin OrganizationService -> updateOrganizationById() method");
//        ResponseModel<OrganizationResponseDTO> response = new ResponseModel<>();
//
//        try {
//            // Fetch the existing organization
//            Organization existingOrganization = organizationRepository.findById(id)
//                    .orElseThrow(() -> new RecordNotFoundException("Organization not found with ID: " + id));
//
//            UUID createdBy = existingOrganization.getCreatedBy();
//            Organization updatedOrganization = mapToEntity(organizationRequestDTO, createdBy);
//
//            // Preserve essential fields
//            updatedOrganization.setId(existingOrganization.getId());
//            updatedOrganization.setCreatedBy(createdBy);
//            updatedOrganization.setOrganizationSequenceId(existingOrganization.getOrganizationSequenceId());
//            updatedOrganization.setModifiedBy(createdBy);
//            updatedOrganization.setModifiedOn(Timestamp.valueOf(LocalDateTime.now()));
//
//            validatePaymentOptions(organizationRequestDTO);
//
//            // Initialize payment details
//            PaymentDetails paymentDetails = new PaymentDetails();
//
//            // Handling prepaid payment details
//            paymentDetails.setPrepaidValues(organizationRequestDTO.getPaymentDetails().getPrepaid().stream()
//                    .map(Prepaid::getPrepaidAdvance)
//                    .collect(Collectors.toList()));
//            paymentDetails.setPrepaidPaymentModes(organizationRequestDTO.getPaymentDetails().getPrepaid().stream()
//                    .map(Prepaid::getPaymentMode)
//                    .collect(Collectors.toList()));
//
//            // Handling postpaid payment details
//            paymentDetails.setPostpaidValues(organizationRequestDTO.getPaymentDetails().getPostPaid().stream()
//                    .map(PostPaid::getPostPaidCreditLimit)
//                    .collect(Collectors.toList()));
//            paymentDetails.setPostpaidPaymentModes(organizationRequestDTO.getPaymentDetails().getPostPaid().stream()
//                    .map(PostPaid::getPaymentMode)
//                    .collect(Collectors.toList()));
//
//            paymentDetails.setOrganization(updatedOrganization);
//            updatedOrganization.setPaymentDetails(paymentDetails);
//
//            // Ensure lab and role are set
//            Optional<Lab> labOptional = labRepository.findById(organizationRequestDTO.getLabId());
//            updatedOrganization.setLab(labOptional.get());
//
//            Optional<Role> roleOptional = roleRepository.findById(organizationRequestDTO.getRoleId());
//            updatedOrganization.setRole(roleOptional.get());
//
//            // Initialize configurations
//            PatientConfiguration patientConfig = new PatientConfiguration();
//            BeanUtils.copyProperties(organizationRequestDTO.getPatientConfiguration(), patientConfig);
//            patientConfig.setOrganization(updatedOrganization);
//            updatedOrganization.setPatientConfiguration(patientConfig);
//
//            ReportAccessConfiguration reportConfig = new ReportAccessConfiguration();
//            BeanUtils.copyProperties(organizationRequestDTO.getReportAccessConfiguration(), reportConfig);
//            reportConfig.setOrganization(updatedOrganization);
//            updatedOrganization.setReportAccessConfiguration(reportConfig);
//
//            BillAccessConfiguration billConfig = new BillAccessConfiguration();
//            BeanUtils.copyProperties(organizationRequestDTO.getBillAccessConfiguration(), billConfig);
//            billConfig.setOrganization(updatedOrganization);
//            updatedOrganization.setBillAccessConfiguration(billConfig);
//
//            Organization savedEntity = organizationRepository.save(updatedOrganization);
//            OrganizationResponseDTO savedDTO = mapToResponseDTO(savedEntity);
//
//            response.setStatusCode(HttpStatus.OK.toString());
//            response.setMessage("Organization updated successfully.");
//            response.setData(savedDTO);
//
//            log.info("Organization updated successfully with ID: {}", savedDTO.getId());
//        } catch (Exception e) {
//            log.error("Error occurred while updating Organization: {}", e.getMessage());
//            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
//            response.setMessage("An error occurred while updating the Organization: " + e.getMessage());
//        }
//
//        return response;
//    }
    
    @Override
    public ResponseModel<OrganizationResponseDTO> updateOrganizationById(UUID id, OrganizationRequestDTO organizationDTO) {
        log.info("Begin OrganizationService -> updateOrganizationById() method");
        ResponseModel<OrganizationResponseDTO> response = new ResponseModel<>();

        try {
            // Fetch the existing Organization by ID
            Optional<Organization> existingOrganizationOpt = organizationRepository.findById(id);
            if (!existingOrganizationOpt.isPresent()) {
                log.error("Organization with ID {} not found", id);
                response.setStatusCode(HttpStatus.NOT_FOUND.toString());
                response.setMessage("Organization not found.");
                response.setData(null);
                return response;
            }

            Organization existingOrganization = existingOrganizationOpt.get();

            // Map new data to the existing entity
            BeanUtils.copyProperties(organizationDTO, existingOrganization, "id", "organizationSequenceId", "createdBy", "paymentDetails", "patientConfiguration", "reportAccessConfiguration", "billAccessConfiguration");

            // Update PaymentDetails if provided
            if (organizationDTO.getPaymentDetails() != null) {
                PaymentDetails paymentDetails = existingOrganization.getPaymentDetails() != null ? existingOrganization.getPaymentDetails() : new PaymentDetails();

                // Handling prepaid payment details
                if (organizationDTO.getPaymentDetails().getPrepaid() != null) {
                    paymentDetails.setPrepaidValues(organizationDTO.getPaymentDetails().getPrepaid().stream()
                            .map(Prepaid::getPrepaidAdvance)
                            .collect(Collectors.toList()));
                    paymentDetails.setPrepaidPaymentModes(organizationDTO.getPaymentDetails().getPrepaid().stream()
                            .map(Prepaid::getPaymentMode)
                            .collect(Collectors.toList()));
                }

                // Handling postpaid payment details
                if (organizationDTO.getPaymentDetails().getPostPaid() != null) {
                    paymentDetails.setPostpaidValues(organizationDTO.getPaymentDetails().getPostPaid().stream()
                            .map(PostPaid::getPostPaidCreditLimit)
                            .collect(Collectors.toList()));
                    paymentDetails.setPostpaidPaymentModes(organizationDTO.getPaymentDetails().getPostPaid().stream()
                            .map(PostPaid::getPaymentMode)
                            .collect(Collectors.toList()));
                }

                paymentDetails.setOrganization(existingOrganization);
                existingOrganization.setPaymentDetails(paymentDetails);
            }

            // Ensure lab and role are set if provided
            if (organizationDTO.getLabId() != null) {
                Optional<Lab> labOptional = labRepository.findById(organizationDTO.getLabId());
                if (labOptional.isPresent()) {
                    existingOrganization.setLab(labOptional.get());
                } else {
                    throw new IllegalArgumentException("Lab with ID " + organizationDTO.getLabId() + " not found.");
                }
            }

            if (organizationDTO.getRoleId() != null) {
                Optional<Role> roleOptional = roleRepository.findById(organizationDTO.getRoleId());
                if (roleOptional.isPresent()) {
                    existingOrganization.setRole(roleOptional.get());
                } else {
                    throw new IllegalArgumentException("Role with ID " + organizationDTO.getRoleId() + " not found.");
                }
            }

            // Update configurations if provided
            if (organizationDTO.getPatientConfiguration() != null) {
                PatientConfiguration patientConfig = existingOrganization.getPatientConfiguration() != null ? existingOrganization.getPatientConfiguration() : new PatientConfiguration();
                BeanUtils.copyProperties(organizationDTO.getPatientConfiguration(), patientConfig);
                patientConfig.setOrganization(existingOrganization);
                existingOrganization.setPatientConfiguration(patientConfig);
            }

            if (organizationDTO.getReportAccessConfiguration() != null) {
                ReportAccessConfiguration reportConfig = existingOrganization.getReportAccessConfiguration() != null ? existingOrganization.getReportAccessConfiguration() : new ReportAccessConfiguration();
                BeanUtils.copyProperties(organizationDTO.getReportAccessConfiguration(), reportConfig);
                reportConfig.setOrganization(existingOrganization);
                existingOrganization.setReportAccessConfiguration(reportConfig);
            }

            if (organizationDTO.getBillAccessConfiguration() != null) {
                BillAccessConfiguration billConfig = existingOrganization.getBillAccessConfiguration() != null ? existingOrganization.getBillAccessConfiguration() : new BillAccessConfiguration();
                BeanUtils.copyProperties(organizationDTO.getBillAccessConfiguration(), billConfig);
                billConfig.setOrganization(existingOrganization);
                existingOrganization.setBillAccessConfiguration(billConfig);
            }

            // Save updated organization entity
            Organization updatedEntity = organizationRepository.save(existingOrganization);
            OrganizationResponseDTO updatedDTO = mapToResponseDTO(updatedEntity);

            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Organization updated successfully.");
            response.setData(updatedDTO);

            log.info("Organization updated successfully with ID: {}", updatedDTO.getId());
        } catch (Exception e) {
            log.error("Error occurred while updating Organization: {}", e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setMessage("An error occurred while updating the Organization: " + e.getMessage());
        }

        return response;
    }




    @Override
    public ResponseModel<OrganizationResponseDTO> deleteOrganizationById(UUID id) {
        log.info("Begin OrganizationServiceImpl -> deleteOrganizationById() method with ID: {}", id);

        ResponseModel<OrganizationResponseDTO> response = new ResponseModel<>();
        try {
            Organization organization = organizationRepository.findById(id)
                    .orElseThrow(() -> new RecordNotFoundException("Organization not found with ID: " + id));

            organizationRepository.deleteById(id);

            OrganizationResponseDTO organizationResponseDTO = mapToResponseDTO(organization);

            response.setStatusCode(HttpStatus.OK.toString());
            response.setMessage("Organization deleted successfully with ID: " + id);
            response.setData(organizationResponseDTO);

            log.info("Organization with ID: {} deleted successfully.", id);

        } catch (RecordNotFoundException e) {
            log.error("Organization deletion failed: {}", e.getMessage());
            response.setStatusCode(HttpStatus.NOT_FOUND.toString());
            response.setMessage(e.getMessage());
            response.setData(null);
        } catch (Exception e) {
            log.error("Error occurred while deleting organization with ID {}: {}", id, e.getMessage());
            response.setMessage("Failed to delete organization: " + e.getMessage());
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
            response.setData(null);
        }

        log.info("End OrganizationServiceImpl -> deleteOrganizationById() method");
        return response;
    }
    
}