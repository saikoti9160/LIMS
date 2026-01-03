package com.digiworldexpo.lims.master.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.Relation;

@Repository
public interface RelationRepository extends JpaRepository<Relation, UUID> {
	
	Optional<Relation> findByRelationName(String relationName);

	@Query("SELECT r FROM Relation r WHERE r.active=true AND LOWER(COALESCE(r.relationName,'')) LIKE LOWER(CONCAT('%', :startsWith, '%'))")
	List<Relation> getAllRelationNames(@Param("startsWith") String startsWith, Sort sortedBy);
	
	@Query("SELECT r FROM Relation r WHERE r.active=true")
	List<Relation> findAllByActive(Sort sortedBy);
}
