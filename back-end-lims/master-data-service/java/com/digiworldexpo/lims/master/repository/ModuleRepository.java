//package com.digiworldexpo.lims.master.repository;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import org.springframework.data.domain.Sort;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import com.digiworldexpo.lims.entities.master.Module;
//
//@Repository
//public interface ModuleRepository extends JpaRepository<Module, UUID> {
//
//	@Query("SELECT m FROM Module m WHERE m.createdBy = :createdBy AND LOWER(m.moduleName) = LOWER(:moduleName)")
//	Optional<Module> findByModuleNameAndCreatedBy(@Param("createdBy") UUID createdBy, @Param("moduleName") String roleName);
//	
//	@Query("SELECT m FROM Module m where m.active=true AND (LOWER(COALESCE(m.moduleName,'')) LIKE LOWER(CONCAT('%',:startsWith,'%')))")
//	List<Module> getAllModulesByParams(@Param("startsWith") String startsWith, Sort sortedBy);
//	
//	@Query("SELECT m FROM Module m where m.active=true")
//	List<Module> findAllByActive(Sort sortedBy);
//}
