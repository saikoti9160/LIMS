package com.digiworldexpo.lims.authentication.repository;

import java.util.List;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.authentication.model.UserDTO;
import com.digiworldexpo.lims.entities.User;

@Repository
@EnableJpaRepositories
public interface UserAuthenticationRepository extends JpaRepository<User, UUID> {

	Optional<User> findByEmail(String email);
	
	    @Query(value = "SELECT " +
	    				"u.id AS user_id, " +
	    				"CONCAT(u.first_name, ' ', u.last_name) AS name, " +
	                   "r.role_name AS role_name, " +
	                   "a.account_name AS account_name " +
	                   "FROM identity.user u " +
	                   "LEFT JOIN masterdata.role r ON u.role_id = r.id " +
	                   "JOIN masterdata.account a ON u.account = a.id " +
	                   "WHERE u.email = :email " +
	                   "GROUP BY r.role_name, a.account_name, u.id, u.first_name", 
	           nativeQuery = true)
	    List<Object[]> findRoleAndAccountTypeEmail(@Param("email") String email);


//	List<Object[]> findRoleAndAccountTypeEmail(String email);

	boolean existsByEmail(String email);
	
	@Query(value = "SELECT LPAD(CAST(nextval('user_sequence_id') AS TEXT), 4, '0')", nativeQuery = true)
	String getNextFormattedUserSequenceId();

	Page<User> findByCreatedByAndActive(UUID createdBy, boolean active, Pageable pageable);

//    Page<User> findByCreatedByAndFirstNameStartingWithIgnoreCaseAndActive(
//        UUID createdBy, String startsWith, boolean active, Pageable pageable
//    );

    long countByCreatedByAndActive(UUID createdBy, boolean active);

//	
//	@Query("SELECT u.email AS email, u.phone AS phone, u.labName AS labName, u.country AS country, u.state AS state, u.city AS city, u.address AS address " +
//		       "FROM User u WHERE u.email = :email")
//		Object[] getUserByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u WHERE u.createdBy = :createdBy AND u.active = true AND " +
    	       "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    	       "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    	       "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    	       "LOWER(u.userSequenceId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
    	       "LOWER(u.role.roleName) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    	Page<User> searchByKeyword(@Param("createdBy") UUID createdBy, 
    	                           @Param("keyword") String keyword, 
    	                           Pageable pageable);


}
