package com.shivu.swiggy_app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shivu.swiggy_app.entity.DeliveryPartner;
import com.shivu.swiggy_app.repository.DeliveryPartnerRepository;


@Service
public class DeliverypartnerServiceImpl implements IDeliveryPartnerService {

	@Autowired
	private DeliveryPartnerRepository deliveryPartnerRepository;

	@Override
	public DeliveryPartner create(DeliveryPartner deliveryPartner) {

		return deliveryPartnerRepository.save(deliveryPartner);
	}

	@Override
	public DeliveryPartner update(DeliveryPartner deliveryPartner) {
		// TODO Auto-generated method stub
		return deliveryPartnerRepository.save(deliveryPartner);
	}

	@Override
	public DeliveryPartner getByd(Integer deliveryPartnerId) {
		return deliveryPartnerRepository.findById(deliveryPartnerId).orElse(null);
	}

	@Override
	public void deletById(Integer deliveryPartnerId) {

		deliveryPartnerRepository.deleteById(deliveryPartnerId);

	}

	@Transactional
	@Override
	public DeliveryPartner getByEmail(String email) {
		
		return deliveryPartnerRepository.findByEmail(email).orElse(null);
		
	}
	
	@Override
	public DeliveryPartner findByPasswordResetToken(String token) {
		// TODO Auto-generated method stub
		return deliveryPartnerRepository.findByPasswordResetToken(token);
	}

}
