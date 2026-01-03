package com.digiworldexpo.lims.master.service.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.digiworldexpo.lims.entities.master.Permission;
import com.digiworldexpo.lims.entities.master.Role;
import com.digiworldexpo.lims.master.exception.BadRequestException;
import com.digiworldexpo.lims.master.exception.DuplicateRecordFoundException;
import com.digiworldexpo.lims.master.exception.RecordNotFoundException;
import com.digiworldexpo.lims.master.model.response.ResponseModel;
import com.digiworldexpo.lims.master.repository.RoleRepository;
import com.digiworldexpo.lims.master.service.RoleService;
import com.digiworldexpo.lims.master.util.MasterDataLoader;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService{
	
	private final RoleRepository roleRepository;
	private final MasterDataLoader masterDataLoader;
	

	
	public RoleServiceImpl(RoleRepository roleRepository, MasterDataLoader masterDataLoader) {
		this.roleRepository = roleRepository;
		this.masterDataLoader = masterDataLoader;
	}

	@Override
	public ResponseModel<Role> saveRole(Role role, UUID createdBy) {
	log.info("Begin of Role Service Implementation -> saveRole() method");
	ResponseModel<Role> responseModel = new ResponseModel<Role>();
	try {
		if (role.getRoleName() == null || role.getRoleName().isEmpty()) {
		    throw new BadRequestException("Provided Role name field must be filled");
		}
		
		if(role.getRoleName().trim().isEmpty() || role.getRoleName().charAt(0)==' ') {
			throw new IllegalArgumentException("Role name must not contain a space as first letter");
		}
		
		Optional<Role> optionaRole = roleRepository.findByRoleNameAndCreatedBy(createdBy, role.getRoleName());
		if(optionaRole.isPresent()) {
			throw new DuplicateRecordFoundException("Role data for this "+role.getRoleName()+" is already exists");
		}
		
		role.setCreatedBy(createdBy);
		masterDataLoader.addRole(role);
		log.info("New role data has been added in the cache");
		if (role.getPermission() != null && !role.getPermission().isEmpty()) {
		    for (Permission permission : role.getPermission()) {
		        permission.setRole(role);  
		    }
		}

		//Saving the Role object in the database in Role table
		roleRepository.save(role);
		responseModel.setStatusCode(HttpStatus.CREATED.toString());
		responseModel.setMessage("Role data has been created successfully in the database");
		responseModel.setData(role);
		
	} catch(IllegalArgumentException illegalArgumentException) {
		log.info("Error occured due to invaid argument {}", illegalArgumentException.getMessage());
		responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		responseModel.setMessage("Error occured due to invaid argument "+ illegalArgumentException.getMessage());
		responseModel.setData(null);
	} catch (BadRequestException badRequestException) {
		log.info("Validation failed for role name input: {}", badRequestException.getMessage());
		responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
		responseModel.setMessage("Validation failed for role name input: "+ badRequestException.getMessage());
		responseModel.setData(null);
	} catch (DuplicateRecordFoundException duplicateRecordFoundException) {
		log.info("Duplicate Record found : {}", duplicateRecordFoundException.getMessage());
		responseModel.setStatusCode(HttpStatus.CONFLICT.toString());
		responseModel.setMessage("Duplicate Record found : "+ duplicateRecordFoundException.getMessage());
		responseModel.setData(null);
	} catch (Exception exception) {
		log.info("Error in saveRole(): {}", exception.getMessage());
		responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
		responseModel.setMessage("Error in saveRole(): "+ exception.getMessage());
		responseModel.setData(null);
	}
	log.info("End of Role Service Implementation -> saveRole() method");
	return responseModel;
	}

	@Override
	public ResponseModel<Role> getRoleById(UUID id) {
		log.info("Begin of Role Service Implementation -> getRoleById() method");
		ResponseModel<Role> responseModel = new ResponseModel<Role>();
		try {
			Role existedRoleData = roleRepository.findById(id)
					.orElseThrow(() -> new RecordNotFoundException("No Role data found for this id: "+ id));
			responseModel.setStatusCode(HttpStatus.OK.toString());
			responseModel.setMessage("Role data has been fetched successfully for this id: " + id);
			responseModel.setData(existedRoleData);
			
		} catch (RecordNotFoundException recordNotFoundException) {
			log.info("No record found for the given Id: {}", recordNotFoundException.getMessage());
			responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
			responseModel.setMessage("No record found for the given Id: "+ recordNotFoundException.getMessage());
			responseModel.setData(null);

		} catch (Exception exception) {
			log.info("Error in getRoleById(): {}", exception.getMessage());
			responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
			responseModel.setMessage("Error in getRoleById(): "+ exception.getMessage());
			responseModel.setData(null);
		}
		log.info("End of Role Service Implementation -> getRoleById() method");
		return responseModel;
	}

	  @Override
	  @Transactional
	    public ResponseModel<List<Role>> getAllRoles(String startsWith, Boolean status, UUID labId, int pageNumber, int pageSize, String sortedBy) {
	        log.info("Begin of Role Service Implementation -> getAllRoles() method");
	        ResponseModel<List<Role>> responseModel = new ResponseModel<>();

	        try {
	            List<Role> allRolesInfo = masterDataLoader.getRoles(); 

	            if (labId != null) {
	                allRolesInfo = allRolesInfo.stream()
	                        .filter(role -> labId.equals(role.getLab()))
	                        .collect(Collectors.toList());
	            } else { 
	                allRolesInfo = allRolesInfo.stream()
	                        .filter(role -> role.getLab() == null) 
	                        .collect(Collectors.toList());
	            }


	            List<Role> filteredRoleData;
	            if (startsWith != null && !startsWith.isEmpty()) {
	                filteredRoleData = allRolesInfo.stream()
	                        .filter(role -> role.getRoleName().toLowerCase().startsWith(startsWith.toLowerCase()))
	                        .collect(Collectors.toList());
	            } else {
	                filteredRoleData = new ArrayList<>(allRolesInfo); // Create a copy to avoid modifying the original list
	            }

	            if (status != null) {
	                filteredRoleData = filteredRoleData.stream()
	                        .filter(role -> role.isActive() == status)
	                        .collect(Collectors.toList());
	            }
	           
	            filteredRoleData = filteredRoleData.stream()
	                    .sorted(applyDynamicSorting(sortedBy))
	                    .collect(Collectors.toList());

	            
	            Pageable pageable = PageRequest.of(pageNumber, pageSize);
	            int start = (int) pageable.getOffset();
	            int end = Math.min(start + pageable.getPageSize(), filteredRoleData.size());
	            if (start > filteredRoleData.size()) {
	                throw new IllegalArgumentException("Page number exceeds available data.");
	            }
	            List<Role> paginatedList = filteredRoleData.subList(start, end);

	            responseModel.setStatusCode(HttpStatus.OK.toString());
	            responseModel.setMessage("All roles data has been retrieved successfully.");
	            responseModel.setData(paginatedList);
	            responseModel.setTotalCount(filteredRoleData.size());
	            responseModel.setPageNumber(pageNumber);
	            responseModel.setPageSize(pageSize);
	            responseModel.setSortedBy("Sorted all the Roles data based on " + sortedBy + " in ascending order");

	        }catch (IllegalArgumentException e) {
	            log.error("Invalid input in getAllRoles: {}", e.getMessage());
	            responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	            responseModel.setMessage("Invalid input: " + e.getMessage());
	            responseModel.setData(null);
	        } catch (RecordNotFoundException e) { 
	            log.error("Record not found in getAllRoles: {}", e.getMessage());
	            responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
	            responseModel.setMessage("Record not found: " + e.getMessage());
	            responseModel.setData(null);
	        } catch (Exception e) { 
	            log.error("Internal server error in getAllRoles: {}", e.getMessage());
	            responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	            responseModel.setMessage("Internal server error: " + e.getMessage());
	            responseModel.setData(null);
	        }
	        log.info("End of Role Service Implementation -> getAllRoles() method");
	        return responseModel;
	    }

	  @Override
	  public ResponseModel<Role> updateRoleById(UUID id, Role newRoleData, UUID modifiedBy) {
	      log.info("Begin of Role Service Implementation -> updateRoleById() method");
	      ResponseModel<Role> responseModel = new ResponseModel<>();

	      try {
	         
	          if (newRoleData.getRoleName() == null || newRoleData.getRoleName().isEmpty()) {
	              throw new BadRequestException("Provided Role name field must be filled");
	          }
	          
	          Role existedRole = roleRepository.findById(id)
	                  .orElseThrow(() -> new RecordNotFoundException("No Role data found for Id: " + id));
	          
	          Optional<Role> optionalRole = roleRepository.findByRoleNameAndCreatedBy(newRoleData.getCreatedBy(), newRoleData.getRoleName());
	          if (optionalRole.isPresent() && !optionalRole.get().getId().equals(existedRole.getId())) {
	              throw new DuplicateRecordFoundException("Role data for this " + optionalRole.get().getRoleName() + " already exists");
	          }

	          existedRole.setRoleName(newRoleData.getRoleName());
	          existedRole.setActive(newRoleData.isActive());
	          existedRole.setModifiedBy(modifiedBy);
	          existedRole.setModifiedOn(new Timestamp(System.currentTimeMillis()));

	          Map<UUID, Permission> existingPermissionsMap = existedRole.getPermission().stream()
	                  .collect(Collectors.toMap(Permission::getId, p -> p));

	          List<Permission> updatedPermissions = new ArrayList<>();

	          for (Permission newPermission : newRoleData.getPermission()) {
	              if (newPermission.getId() != null && existingPermissionsMap.containsKey(newPermission.getId())) {
	                  // **Update Existing Permission**
	                  Permission existingPermission = existingPermissionsMap.get(newPermission.getId());
	                  existingPermission.setModuleName(newPermission.getModuleName());
	                  existingPermission.setCanCreate(newPermission.isCanCreate());
	                  existingPermission.setCanRead(newPermission.isCanRead());
	                  existingPermission.setCanUpdate(newPermission.isCanUpdate());
	                  existingPermission.setCanDelete(newPermission.isCanDelete());

	                  updatedPermissions.add(existingPermission);
	                  existingPermissionsMap.remove(newPermission.getId());
	              } else {
	                  newPermission.setRole(existedRole); 
	                  updatedPermissions.add(newPermission);
	              }
	          }

	          for (Permission permissionToRemove : existingPermissionsMap.values()) {
	              existedRole.getPermission().remove(permissionToRemove);
	          }

	          existedRole.setPermission(updatedPermissions);

	          roleRepository.save(existedRole);

	          if (masterDataLoader != null) {
	              masterDataLoader.updateRole(existedRole);
	              log.info("Role updated in the cache");
	          } else {
	              log.error("masterDataLoader is null");
	              throw new Exception("Failed to update role in the cache due to internal error.");
	          }

	          responseModel.setStatusCode(HttpStatus.OK.toString());
	          responseModel.setMessage("Role has been updated successfully for this id: " + id);
	          responseModel.setData(existedRole);

	      } catch (BadRequestException | DuplicateRecordFoundException | RecordNotFoundException e) {
	          log.info("Validation or data error: {}", e.getMessage());
	          responseModel.setStatusCode(HttpStatus.BAD_REQUEST.toString());
	          responseModel.setMessage(e.getMessage());
	          responseModel.setData(null);
	      } catch (Exception exception) {
	          log.error("Error in updateRoleById(): {}", exception.getMessage());
	          responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	          responseModel.setMessage("Error in updateRoleById(): " + exception.getMessage());
	          responseModel.setData(null);
	      }

	      log.info("End of Role Service Implementation -> updateRoleById() method");
	      return responseModel;
	  }

	  @Override
	  public ResponseModel<Role> deleteRoleById(UUID id) {
	      log.info("Begin of Role Service Implementation -> deleteRoleById() method");
	      ResponseModel<Role> responseModel = new ResponseModel<Role>();
	      try {
	          Role existedRoleData = roleRepository.findById(id)
	                  .orElseThrow(() -> new RecordNotFoundException("No Role data found for this id: " + id));

	          existedRoleData.setActive(false); 
	          
	          roleRepository.save(existedRoleData);
	          // Removing from Cache
	          if (masterDataLoader != null) {
	        	  masterDataLoader.updateRole(existedRoleData);
	              log.info("Role removed from cache successfully.");
	          } else {
	              log.error("masterDataLoader is null. Failed to remove role from cache.");
	              throw new Exception("Failed to update cache due to an internal error.");
	          }

	          responseModel.setStatusCode(HttpStatus.OK.toString());
	          responseModel.setMessage("The role with ID: " + id + " has been successfully deactivated.");
	          responseModel.setData(existedRoleData);
	          
	      } catch (RecordNotFoundException recordNotFoundException) {
	          log.error("No record found for the given Id: {}", recordNotFoundException.getMessage());
	          responseModel.setStatusCode(HttpStatus.NOT_FOUND.toString());
	          responseModel.setMessage("No record found for the given Id: " + recordNotFoundException.getMessage());
	          responseModel.setData(null);

	      } catch (Exception exception) {
	          log.error("Error in deleteRoleById(): {}", exception.getMessage());
	          responseModel.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
	          responseModel.setMessage("Error in deleteRoleById(): " + exception.getMessage());
	          responseModel.setData(null);
	      }
	      log.info("End of Role Service Implementation -> deleteRoleById() method");
	      return responseModel;
	  }

	
//	private List<Role> getRoleBasedOnStartsWithAndCreatedBy(List<Role> allRolesInfo, String startsWith, UUID createdBy, String sortedBy){
//		log.info("Begin of Role Service Implementation -> getRoleBasedOnStartsWithAndCreatedBy() method");
//		
//		List<Role> filteredData = allRolesInfo.stream()
//				.filter(role -> role.getRoleName().toLowerCase().contains(startsWith.toLowerCase()) &&
//						role.getCreatedBy() != null && role.getCreatedBy().equals(createdBy))
//				.sorted(applyDynamicSorting(sortedBy)).collect(Collectors.toList());
//		
//		log.info("End of Role Service Implementation -> getRoleBasedOnStartsWithAndCreatedBy() method");
//		return filteredData;
//	}
//	
	
	
	private Comparator<Role> applyDynamicSorting(String sortedBy){
		log.info("Begin of Role Service Implementation -> applyDynamicSorting() method");
		
		Map<String, Comparator<Role>> sortMapping = new HashMap<String, Comparator<Role>>();
		
		sortMapping.put("roleName", Comparator.comparing(Role::getRoleName));
		sortMapping.put("createdBy", Comparator.comparing(Role::getCreatedBy));
		sortMapping.put("createdOn", Comparator.comparing(Role::getCreatedOn));
		
		Comparator<Role> comparator = sortMapping.getOrDefault(sortedBy, Comparator.comparing(Role::getRoleName));
		log.info("End of Role Service Implementation -> applyDynamicSorting() method");
		return comparator;
	}
}
