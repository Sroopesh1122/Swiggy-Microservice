package com.shivu.swiggy_app.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.shivu.swiggy_app.request.Deliveries;

@FeignClient(name = "swiggy-deliveriesService")
public interface DeliveriesServiceClient {

	@PostMapping("/api/deliveries/create")
	public Deliveries createDelivery(Deliveries deliveries);
	
	
	@GetMapping("/api/deliveries/id/{id}")
	public Deliveries getById(@PathVariable Integer id);
	
	@GetMapping("/api/deliveries/orderId/{id}")
	public Deliveries getByOrderId(@PathVariable Integer id);
	
}
