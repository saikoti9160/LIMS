package com.digiworldexpo.lims.master.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Permission;

import java.util.UUID;

@Repository  
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    
}