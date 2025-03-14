package com.shivu.swiggy_app.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.shivu.swiggy_app.request.DeliveryPartner;

@FeignClient(name = "swiggy-deliveryPartnerService")
public interface DeliveryPartnerServiceClient {

	@GetMapping("/delivery/partnerId/{id}")
	DeliveryPartner getById(@PathVariable Integer id);
	
	@GetMapping("/delivery/id/{id}")
	DeliveryPartner getPartner(@PathVariable Integer id);
	
	
	@GetMapping("/delivery/email")
	DeliveryPartner getBYEmail(@RequestParam String email);
	
}
