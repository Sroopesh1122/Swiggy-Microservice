package com.shivu.swiggy_app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shivu.swiggy_app.entity.DeliveryPartner;

@Repository
public interface DeliveryPartnerRepository extends JpaRepository<DeliveryPartner, Integer> {

	public Optional<DeliveryPartner> findByEmail(String email);
	
	public DeliveryPartner findByPasswordResetToken(String token);
	
}
