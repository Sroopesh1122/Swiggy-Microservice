package com.shivu.swiggy_app.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.shivu.swiggy_app.entity.Deliveries;
import com.shivu.swiggy_app.repository.DeliveriesRepository;

@Service
public class DeliveriesServiceImpl implements IDeliveriesServices {

	@Autowired
	private DeliveriesRepository deliveriesRepository;
	
	@Override
	public Deliveries create(Deliveries delivery) {
		
		return deliveriesRepository.save(delivery);
	}

	@Override
	public Deliveries update(Deliveries delivery) {
		// TODO Auto-generated method stub
		return deliveriesRepository.save(delivery);
	}

	@Override
	public Deliveries getById(Integer deliveryId) {
		// TODO Auto-generated method stub
		
		Optional<Deliveries> deliveryOptional = deliveriesRepository.findById(deliveryId);
		
		if(deliveryOptional.isEmpty())
		{
			return null;
		}
		
		return deliveryOptional.get();
	}

	@Override
	public void deleteById(Integer deliveryId) {
	
		deliveriesRepository.deleteById(deliveryId);

	}

	@Override
	public Page<Deliveries> getDeliveriesByDeliveryPartnerId(Integer deliveryPartnerId,String q ,Pageable pageable) {
		// TODO Auto-generated method stub
		return deliveriesRepository.findAllDeliveriesByPartnerId(deliveryPartnerId,q,pageable);
	}

	@Override
	public Deliveries getByOrderId(Integer orderId) {
		// TODO Auto-generated method stub
		return deliveriesRepository.findDeliveryByOrderId(orderId);
	}

}
