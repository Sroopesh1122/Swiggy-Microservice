package com.shivu.swiggy_app.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.shivu.swiggy_app.request.Order;

@FeignClient(name = "swiggy-orderService")
public interface OrderServiceClient {


	@GetMapping("/api/order/orderId")
	public Order getById(@RequestParam Integer orderId);
	
	
	@PutMapping("/api/order/update")
	public Order updateOrder(@RequestBody Order order);
	
}
