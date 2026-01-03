package com.digiworldexpo.lims.lab.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.lab_management.Rack;

@Repository
public interface RackRepository extends JpaRepository<Rack, UUID> {

    @Query("SELECT r FROM Rack r WHERE "
         + "(:searchText IS NULL OR :searchText = '' OR LOWER(r.rackNumber) LIKE LOWER(CONCAT('%', :searchText, '%'))) ")
    Page<Rack> findRacksWithSearchText(@Param("searchText") String searchText, Pageable pageable);

	Page<Rack> findAllByLabId(UUID labId, PageRequest pageable);
}
