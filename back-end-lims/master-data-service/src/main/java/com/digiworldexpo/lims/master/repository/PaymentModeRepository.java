package com.digiworldexpo.lims.master.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.digiworldexpo.lims.entities.master.PaymentMode;

@Repository
public interface PaymentModeRepository extends JpaRepository<PaymentMode, UUID> {
	
	Optional<PaymentMode> findByPaymentModeName(String paymentModeName);

	@Query("SELECT p FROM PaymentMode p WHERE p.active=true AND LOWER(COALESCE(p.paymentModeName,'')) LIKE LOWER(CONCAT('%', :startsWith, '%'))")
	List<PaymentMode> getAllPaymentModeNames(@Param("startsWith") String startsWith, Sort sort);
	
	@Query("SELECT p FROM PaymentMode p WHERE p.active=true")
	List<PaymentMode> findAllByActive(Sort sortedBy);
}
