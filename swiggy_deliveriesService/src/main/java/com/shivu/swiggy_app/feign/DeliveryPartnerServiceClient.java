package com.shivu.swiggy_app.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.shivu.swiggy_app.request.DeliveryPartner;

@FeignClient(name = "swiggy-deliveryPartnerService")
public interface DeliveryPartnerServiceClient
{
	@GetMapping("/delivery/partnerId/{id}")
	public DeliveryPartner getDeliveryPartnerById(@PathVariable Integer id);
	
	@GetMapping("/delivery/email")
	public DeliveryPartner getDeliveryPartnerByEmail(@RequestParam String email);
}
