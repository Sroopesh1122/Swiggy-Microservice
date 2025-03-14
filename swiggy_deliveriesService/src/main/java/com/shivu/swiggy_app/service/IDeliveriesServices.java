package com.shivu.swiggy_app.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.shivu.swiggy_app.entity.Deliveries;

public interface IDeliveriesServices{

	public Deliveries create (Deliveries delivery);
	
	public Deliveries update(Deliveries delivery);
	
	public Deliveries getById(Integer deliveryId);
	
	public void deleteById(Integer deliveryId);
	
	public Page<Deliveries> getDeliveriesByDeliveryPartnerId(Integer deliveryPartnerId,String q ,Pageable pageable);
	
	public Deliveries getByOrderId(Integer orderId);
	
	
}
