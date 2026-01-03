package com.digiworldexpo.lims.authentication.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.*;

@Repository
public interface LabRepository extends JpaRepository<Lab, UUID> {

}
