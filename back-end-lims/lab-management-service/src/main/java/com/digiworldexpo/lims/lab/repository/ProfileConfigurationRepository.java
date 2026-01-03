package com.digiworldexpo.lims.lab.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.digiworldexpo.lims.entities.lab_management.ProfileConfiguration;

public interface ProfileConfigurationRepository extends JpaRepository< ProfileConfiguration, UUID> {

}
