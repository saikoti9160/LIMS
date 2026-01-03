package com.digiworldexpo.lims.authentication.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.User;

@Repository
public interface StaffRepository extends JpaRepository<User,UUID> {

}
